package com.pbl.elearning.common.config;

import com.pbl.elearning.common.constant.CommonConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
public class RabbitMQConfig implements WebSocketMessageBrokerConfigurer {

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter());
    return template;
  }

  @Bean
  public FanoutExchange notificationExchange() {
    return new FanoutExchange(CommonConstant.NOTIFICATION_EXCHANGE, true, false);
  }

  @Bean
  public Queue notificationQueue() {
    return new Queue(CommonConstant.NOTIFICATION_QUEUE, true);
  }

  @Bean
  public Binding notificationBinding(Queue notificationQueue, FanoutExchange notificationExchange) {
    return BindingBuilder.bind(notificationQueue).to(notificationExchange);
  }
}