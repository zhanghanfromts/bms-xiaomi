package org.example.bms.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.bms.entity.SignalRequest;
import org.example.bms.entity.VehicleSignal;
import org.example.bms.entity.VehicleSignalDTO;
import org.example.bms.mapper.VehicleSignalMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class VehicleSignalServiceImplTest {

    @Autowired
    private VehicleSignalServiceImpl vehicleSignalService;

    @Autowired
    private VehicleSignalMapper vehicleSignalMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TEST_CID = "test001";
    private static final String TEST_SIGNAL_DATA = "{\"Mx\":12.0,\"Mi\":12.6}";

    @BeforeEach
    void setUp() {
        // 清理测试数据
        redisTemplate.delete("signal:" + TEST_CID);
    }

    @Test
    void testReportSignals() {
        // 准备测试数据
        List<SignalRequest> requests = new ArrayList<>();
        SignalRequest request = new SignalRequest();
        request.setCid(TEST_CID);
        request.setWarnId(1);
        request.setSignalData(TEST_SIGNAL_DATA);
        requests.add(request);

        // 执行测试
        boolean result = vehicleSignalService.reportSignals(requests);

        // 验证结果
        assertTrue(result);
        
        // 验证数据是否写入数据库
        String tableSuffix = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        List<VehicleSignal> signals = vehicleSignalMapper.selectLatestSignal(TEST_CID, tableSuffix);
        assertNotNull(signals);
        assertFalse(signals.isEmpty());
        assertEquals(TEST_CID, signals.get(0).getCid());
        assertEquals(TEST_SIGNAL_DATA, signals.get(0).getSignalData());
    }

    @Test
    void testQueryLatestSignal() {
        // 插入测试数据
        testReportSignals();

        // 执行测试
        List<VehicleSignalDTO> result = vehicleSignalService.queryLatestSignal(TEST_CID);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(TEST_CID, result.get(0).getCid());
        assertEquals(TEST_SIGNAL_DATA, result.get(0).getSignalData());
    }

    @Test
    void testQueryLatestSignalFromCache() {
        // 插入测试数据
        testReportSignals();

        // 第一次查询，会写入缓存
        List<VehicleSignalDTO> firstResult = vehicleSignalService.queryLatestSignal(TEST_CID);
        assertNotNull(firstResult);
        
        // 第二次查询，应该从缓存获取
        List<VehicleSignalDTO> secondResult = vehicleSignalService.queryLatestSignal(TEST_CID);
        assertNotNull(secondResult);
        assertEquals(firstResult.get(0).getSignalData(), secondResult.get(0).getSignalData());
    }

    @Test
    void testQueryLatestSignalNotFound() {
        // 执行测试
        List<VehicleSignalDTO> result = vehicleSignalService.queryLatestSignal("not_exist_cid");
        // 验证结果
        assertNull(result);
    }

    @Test
    void testSaveSignal() throws JsonProcessingException {
        // 准备测试数据
        VehicleSignal signal = new VehicleSignal();
        signal.setCid(TEST_CID);
        signal.setSignalData(TEST_SIGNAL_DATA);
        signal.setWarnId(1);
        signal.setReportTime(new Date());

        boolean b = vehicleSignalService.saveSignal(signal);
        assertTrue(b);
    }
} 