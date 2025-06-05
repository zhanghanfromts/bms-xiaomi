package org.example.bms.mq;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.example.bms.entity.VehicleSignal;
import org.example.bms.service.VehicleSignalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SignalMessageListener implements MessageListenerConcurrently {
    private static final Logger logger = LoggerFactory.getLogger(SignalMessageListener.class);

    @Autowired
    private VehicleSignalService vehicleSignalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt msg : msgs) {
            try {
                // 1. 解析消息内容
                String messageBody = new String(msg.getBody());
                VehicleSignal signal = objectMapper.readValue(messageBody, VehicleSignal.class);
                vehicleSignalService.saveSignal(signal);
            } catch (JsonParseException e){
                logger.error("上报数据格式有误", e);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                logger.error("处理消息失败", e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    
} 