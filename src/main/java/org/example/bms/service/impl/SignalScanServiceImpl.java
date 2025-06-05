package org.example.bms.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.example.bms.entity.VehicleSignal;
import org.example.bms.mapper.VehicleSignalMapper;
import org.example.bms.service.SignalScanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@Service
public class SignalScanServiceImpl implements SignalScanService {

    @Autowired
    private VehicleSignalMapper vehicleSignalMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DefaultMQProducer producer;

    @Value("${rocketmq.topic}")
    private String topic;

    @Override
    public void scanAndSendSignals() {
        try {
            // 计算30秒前的时间
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, -10);
            Date startTime = calendar.getTime();

            // 获取当前月份的表后缀
            String currentTableSuffix = new SimpleDateFormat("yyyyMMdd").format(new Date());

            // 1. 获取最近30秒内的信号数据
            List<VehicleSignal> signals = vehicleSignalMapper.selectLatestSignals(startTime, currentTableSuffix);

            // 2. 遍历信号数据并发送到MQ
            for (VehicleSignal signal : signals) {
                String messageBody = objectMapper.writeValueAsString(signal);
                Message message = new Message(topic, messageBody.getBytes());
                message.setKeys(signal.getCid());

                // 发送消息
                SendResult sendResult = producer.send(message);
                System.out.println("发送信号数据到MQ成功: " + sendResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
