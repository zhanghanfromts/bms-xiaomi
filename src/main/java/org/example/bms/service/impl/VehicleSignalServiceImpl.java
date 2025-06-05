package org.example.bms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.logging.org.slf4j.Logger;
import org.apache.rocketmq.logging.org.slf4j.LoggerFactory;
import org.example.bms.entity.*;
import org.example.bms.mapper.RuleConfigMapper;
import org.example.bms.mapper.VehicleMapper;
import org.example.bms.mapper.VehicleSignalMapper;
import org.example.bms.mapper.VehicleWarnMapper;
import org.example.bms.mq.SignalMessageListener;
import org.example.bms.service.VehicleSignalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.text.SimpleDateFormat;

@Service
public class VehicleSignalServiceImpl implements VehicleSignalService {
    
    @Autowired
    private VehicleMapper vehicleMapper;
    
    @Autowired
    private VehicleSignalMapper vehicleSignalMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RuleConfigMapper ruleConfigMapper;

    @Autowired
    private VehicleWarnMapper vehicleWarnMapper;

    private static final String SIGNAL_CACHE_KEY_PREFIX = "signal:";
    private static final long CACHE_EXPIRE_TIME = 5; // 缓存过期时间（分钟）
    private static final long CACHE_DELETE_DELAY = 1; // 延迟删除时间（秒）

    private static final Logger logger = LoggerFactory.getLogger(SignalMessageListener.class);

    @Override
    @Transactional
    public boolean reportSignals(List<SignalRequest> requests) {
        if (requests.isEmpty() || requests == null) {
            return false;
        }

        for(SignalRequest request : requests) {
            if(request.getCid()==null || request.getCid().isEmpty() || request.getSignalData()==null || request.getSignalData().isEmpty()) {
                return false;
            }
        }

        // 构建信号数据
        List<VehicleSignal> signals = new ArrayList<>();
        for (SignalRequest request : requests) {
            VehicleSignal signal = new VehicleSignal();
            signal.setCid(request.getCid());
            signal.setSignalData(request.getSignalData());
            signal.setReportTime(new Date());
            signal.setWarnId(request.getWarnId() == null ? -1 : request.getWarnId());
            signals.add(signal);
        }

        // 批量插入数据
        String tableSuffix = signals.get(0).getTableSuffix();
        int result = vehicleSignalMapper.batchInsert(signals, tableSuffix);

        // 执行缓存双删
        if (result > 0) {
            for (VehicleSignal signal : signals) {
                String cacheKey = SIGNAL_CACHE_KEY_PREFIX + signal.getCid();
                // 先删除缓存
                redisTemplate.delete(cacheKey);
                // 延迟删除缓存
                redisTemplate.opsForValue().set(cacheKey, null, CACHE_DELETE_DELAY, TimeUnit.SECONDS);
            }
        }

        return result > 0;
    }

    @Override
    public List<VehicleSignalDTO> queryLatestSignal(String cid) {
        // 尝试从缓存获取
        String cacheKey = SIGNAL_CACHE_KEY_PREFIX + cid;
        List<VehicleSignalDTO> cacheResponse = (List<VehicleSignalDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cacheResponse != null) {
            return cacheResponse;
        }
        
        // 获取当前日期的表后缀
        String currentTableSuffix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        // 缓存未命中，从数据库查询
        List<VehicleSignal> signals = vehicleSignalMapper.selectLatestSignal(cid, currentTableSuffix);

        // 如果不存在上报信号数据则返回空
        if (signals == null || signals.isEmpty()) {
            return null;
        }

        // 构建响应列表
        List<VehicleSignalDTO> responseList = new ArrayList<>();
        for (VehicleSignal signal : signals) {
            VehicleSignalDTO response = new VehicleSignalDTO();
            response.setCid(signal.getCid());
            response.setReportTime(signal.getReportTime());
            response.setSignalData(signal.getSignalData());
            response.setWarnId(signal.getWarnId());
            responseList.add(response);
        }

        // 更新缓存
        redisTemplate.opsForValue().set(cacheKey, responseList, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

        return responseList;
    }

    @Override
    public boolean saveSignal(VehicleSignal signal) throws JsonProcessingException {
        // 2. 获取车辆信息（电池类型：三元电池，铁锂电池）
        String cid = signal.getCid();
        String batteryType = vehicleMapper.selectByCid(cid).getBatteryType();

        // 3. 获取规则配置
        List<RuleConfig> rules = new ArrayList<>();
        if(signal.getWarnId() != null && signal.getWarnId() != -1){
            rules.add(ruleConfigMapper.selectByWarnIdAndBatteryType(signal.getWarnId(), batteryType));
        } else {
            rules.addAll(ruleConfigMapper.selectByBatteryType(batteryType));
        }

        // 4. 解析信号数据 -> {"Ii": 11.7, "Ix": 12.0, "Mi": 9.6, "Mx": 11.0}
        JsonNode signalNode = objectMapper.readTree(signal.getSignalData());


        int insert = 0;
        // 5. 对每个规则进行评估
        for (RuleConfig rule : rules) {
            int warnLevel = evaluateRule(rule, signalNode);
            // 6. 生成告警记录
            VehicleWarn warn = new VehicleWarn();
            warn.setCid(cid);
            warn.setBatteryType(batteryType);
            warn.setWarnId(rule.getWarnId());
            warn.setWarnTime(signal.getReportTime());
            warn.setSignalData(signal.getSignalData());
            warn.setWarnLevel(warnLevel);
            if (warnLevel == 5) {
                warn.setWarnName("无报警");
            }else if(warnLevel == -1){
                warn.setWarnName("报警规则解析失败");
            }else{
                warn.setWarnName(rule.getWarnName());
            }
            insert = vehicleWarnMapper.insert(warn);
            logger.info("生成告警记录: cid={}, warnId={}, warnLevel={}", cid, rule.getWarnId(), warnLevel);
        }

        return insert > 0;
    }

    private int evaluateRule(RuleConfig rule, JsonNode signalNode) {
        try {
            // 获取预警规则
            String expression = rule.getRuleExpression();
            // 拆分预警规则
            String[] ruleLevels = expression.split("\n");

            for (String ruleLevel : ruleLevels) {
                String[] parts = ruleLevel.split(",");
                if (parts.length != 2) continue;

                String condition = parts[0].trim();
                int warnLevel = Integer.parseInt(parts[1].trim());

                // 获取信号值
                double value = 0;
                // 判断是电压规则还是电流规则，计算对应的上报差值
                if (condition.contains("Mx−Mi")) {
                    double mx = signalNode.get("Mx").asDouble();
                    double mi = signalNode.get("Mi").asDouble();
                    value = mx - mi;
                } else if (condition.contains("Ix−Ii")) {
                    double ix = signalNode.get("Ix").asDouble();
                    double ii = signalNode.get("Ii").asDouble();
                    value = ix - ii;
                }else{
                    System.out.println("上报信息与规则无法匹配。规则为："+ruleLevel+"。上报信息为："+signalNode);
                }

                // 判断条件
                if (condition.contains("<=")) {
                    String[] threshold = condition.split("<=");
                    if (Double.parseDouble(threshold[0]) <= value) {
                        return warnLevel;
                    }
                } else if (condition.contains("<")) {
                    String[] threshold = condition.split("<");
                    if (value < Double.parseDouble(threshold[1])) {
                        return warnLevel;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("规则评估失败: ruleId={}", rule.getWarnId(), e);
        }
        return -1;
    }


} 