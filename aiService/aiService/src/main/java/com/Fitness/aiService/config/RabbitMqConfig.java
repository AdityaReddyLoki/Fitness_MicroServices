package com.Fitness.aiService.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;


@Configuration
public class RabbitMqConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.key}")
    private String routingKey;
    @Value("${rabbitmq.dead-letter.exchange:fitness.dead-letter.exchange}")
    private String deadLetterExchangeName;
    @Value("${rabbitmq.dead-letter.queue:activity.dead-letter.queue}")
    private String deadLetterQueueName;
    @Value("${rabbitmq.dead-letter.routing-key:activity.dead-letter}")
    private String deadLetterRoutingKey;

    @Bean
    public Queue activityQueue(){

        return new Queue(queue,true);
    }

    @Bean
    public DirectExchange activityExchange(){
        return new DirectExchange(exchange);
    }
    @Bean
    public Binding activityBinding(
            @Qualifier("activityQueue") Queue activityQueue,
            @Qualifier("activityExchange") DirectExchange activityExchange){
        return BindingBuilder.bind(activityQueue).to(activityExchange).with(routingKey);
    }

    @Bean
    public DirectExchange activityDeadLetterExchange() {
        return new DirectExchange(deadLetterExchangeName, true, false);
    }

    @Bean
    public Queue activityDeadLetterQueue() {
        return new Queue(deadLetterQueueName, true);
    }

    @Bean
    public Binding activityDeadLetterBinding(
            @Qualifier("activityDeadLetterQueue") Queue activityDeadLetterQueue,
            @Qualifier("activityDeadLetterExchange") DirectExchange activityDeadLetterExchange) {
        return BindingBuilder.bind(activityDeadLetterQueue)
                .to(activityDeadLetterExchange)
                .with(deadLetterRoutingKey);
    }

    @Bean
    public MethodInterceptor rabbitRetryInterceptor(RabbitTemplate rabbitTemplate) {
        return RetryInterceptorBuilder.stateless()
                .maxRetries(3)
                .backOffOptions(60_000, 2.0, 300_000)
                .recoverer(new RepublishMessageRecoverer(
                        rabbitTemplate, deadLetterExchangeName, deadLetterRoutingKey))
                .build();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MethodInterceptor rabbitRetryInterceptor,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(rabbitRetryInterceptor);
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new JacksonJsonMessageConverter();
    }
}
