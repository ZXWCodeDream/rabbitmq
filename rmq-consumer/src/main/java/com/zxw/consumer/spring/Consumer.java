package com.zxw.consumer.spring;

import com.rabbitmq.client.*;
import entity.MsgInfo;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;


/**
 * ClassName: Consumer
 * Description:
 * rabbitmq整合spring进行使用
 * @author zxw
 * @date 2020/12/1 8:42 下午
 * @since JDK 1.8
 */
@Component
public class Consumer {


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "msg-queue-test",durable = "true",autoDelete = "false"),
                                            exchange = @Exchange(value = "msg-exchange-test",durable = "true",type = "direct"),
                                            key = "msg.update"))
    @RabbitHandler
    public void received(@Payload MsgInfo msgInfo, @Headers Map<String,Object> headers, Channel channel){
        System.out.println("******************开始接受消息***********************");
        System.out.println("接收到的消息："+msgInfo.toString());
        System.out.println("******************结束接受消息***********************");
    }

}
