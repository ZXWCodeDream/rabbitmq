package com.zxw.provider.spring;

import entity.MsgInfo;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * ClassName: Provider
 * Description:
 *
 * rabbitmq整合spring发送消息
 * @author zxw
 * @date 2020/12/1 8:23 下午
 * @since JDK 1.8
 */
@Component
public class Provider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(MsgInfo msgInfo){

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(msgInfo.getMsgId());
        /**
         * exchange: 交换机名称
         * routingKey:与队列绑定的关联key
         * object:要传送的消息
         * correlationData：消息的唯一ID
         */
//        rabbitTemplate.convertAndSend("topic-exchange","topic.update",msgInfo,correlationData);

        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                // 给消息设定过期时间
                message.getMessageProperties().setExpiration("5000");
                return message;
            }
        };
        rabbitTemplate.convertAndSend("topic-exchange","topic.update",msgInfo,messagePostProcessor,correlationData);

        //lambda表达式实现接口
//        rabbitTemplate.convertAndSend("topic-exchange","topic.update",msgInfo,(Message message)->{message.getMessageProperties().setExpiration("5000");return message;},correlationData);

    }

}
