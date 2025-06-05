package org.example.bms.service.impl;

import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.example.bms.entity.Vehicle;
import org.example.bms.entity.VehicleSignal;
import org.example.bms.entity.VehicleWarnDTO;
import org.example.bms.mapper.VehicleMapper;
import org.example.bms.mapper.VehicleSignalMapper;
import org.example.bms.service.VehicleSignalService;
import org.example.bms.service.VehicleWarnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SignalScanServiceImplTest {

    @Autowired
    private SignalScanServiceImpl signalScanService;

    @Autowired
    private VehicleSignalMapper vehicleSignalMapper;

    @Autowired
    private VehicleWarnService vehicleWarnService;

    private static final String TEST_CID = "test001";
    private static final String TEST_SIGNAL_DATA = "{\"Mx\":12.0,\"Mi\":12.6}";

    @Test
    void testScanAndSendSignals_Success(){
        // 插入测试上报数据
        String tableSuffix = new java.text.SimpleDateFormat("yyyyMMdd").format(new Date());
        VehicleSignal signal = new VehicleSignal();
        signal.setCid(TEST_CID);
        signal.setSignalData(TEST_SIGNAL_DATA);
        signal.setWarnId(1);
        signal.setReportTime(new Date());
        vehicleSignalMapper.insert(signal, tableSuffix);

        // 执行测试
        signalScanService.scanAndSendSignals();

        // 获取生成的告警信息
        List<VehicleWarnDTO> vehicleWarnDTOList = vehicleWarnService.queryWarns(TEST_CID);
        // 验证结果
        assertNotNull(vehicleWarnDTOList);
        assertFalse(vehicleWarnDTOList.isEmpty());
        assertEquals(TEST_CID, vehicleWarnDTOList.get(0).getCid());
    }

}
