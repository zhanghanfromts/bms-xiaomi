package org.example.bms.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConsumerConfig {
    
    @Value("${rocketmq.name-server}")
    private String nameServer;
    
    @Value("${rocketmq.consumer.group}")
    private String consumerGroup;
    
    @Value("${rocketmq.topic}")
    private String topic;
    
    @Bean
    public DefaultMQPushConsumer defaultMQPushConsumer(MessageListenerConcurrently messageListener) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setNamesrvAddr(nameServer);
        consumer.subscribe(topic, "*");
        consumer.registerMessageListener(messageListener);
        consumer.start();
        return consumer;
    }

} 