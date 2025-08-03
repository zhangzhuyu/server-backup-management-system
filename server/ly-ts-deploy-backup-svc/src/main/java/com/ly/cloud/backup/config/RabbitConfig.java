package com.ly.cloud.backup.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

//@Component
//@Lazy
public class RabbitConfig {

    @Value("${queueName:queue_1}")
    private String queueName;

    private String DIRECT_EXCHANGE_NAME = "directServerExchange";

    private String QUEUE_ALL = queueName;
//    private String QUEUE_ALL = "queue_all";

    @Bean
    public Queue queue1() {
        return new Queue(QUEUE_ALL);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingDirectExchangeQueue1(Queue queue1, DirectExchange directExchange) {
        return BindingBuilder.bind(queue1).to(directExchange).with(QUEUE_ALL);
    }

    @Bean
    public RabbitTemplate jacksonRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}
