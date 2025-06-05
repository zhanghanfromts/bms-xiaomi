package org.example.bms.config;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {
    
    @Bean
    public DefaultMQProducer defaultMQProducer(
            @Value("${rocketmq.name-server}") String nameServer,
            @Value("${rocketmq.producer.group}") String producerGroup) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServer);
        producer.setRetryTimesWhenSendAsyncFailed(0);
        producer.start();
        return producer;
    }
} 