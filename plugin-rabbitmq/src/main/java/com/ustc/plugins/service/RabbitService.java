package com.ustc.plugins.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;

/**
 * company: guochuang software co.ltd<br>
 * date: 2019/8/14<br>
 * filename: RabbitService<br>
 * <p>
 * description:<br>
 * 消息队列服务
 * </p>
 *
 * @author xie.bowen@ustcinfo.com
 */
@Log4j
@Data
public class RabbitService {

    private RabbitTemplate rabbitTemplate;

    private ObjectMapper objectMapper;

    /**
     * 发送消息
     *
     * @param exchange   路由
     * @param routingKey 通道
     * @param obj        对象
     */
    public void sendRabbitMqMessage(String exchange, String routingKey, Object obj) {
        try {
            rabbitTemplate.setExchange(exchange);
            rabbitTemplate.setRoutingKey(routingKey);
            Message message = MessageBuilder.withBody(objectMapper.writeValueAsBytes(obj)).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
            message.getMessageProperties().setHeader(AbstractJavaTypeMapper.DEFAULT_CONTENT_CLASSID_FIELD_NAME, MessageProperties.CONTENT_TYPE_JSON);
            rabbitTemplate.convertAndSend(message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
    }
}
