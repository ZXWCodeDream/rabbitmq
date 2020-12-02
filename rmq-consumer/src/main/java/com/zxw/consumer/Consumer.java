package com.zxw.consumer;

import com.rabbitmq.client.*;
import entity.MsgInfo;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: Consumer
 * Description:
 *
 * @author zxw
 * @date 2020/12/1 8:42 下午
 * @since JDK 1.8
 */
@Component
public class Consumer {


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "msg-queue",durable = "true",autoDelete = "false"),
                                            exchange = @Exchange(value = "msg-exchange",durable = "true",type = "direct"),
                                            key = "msg.update"))
    @RabbitHandler
    public void received(@Payload MsgInfo msgInfo, @Headers Map<String,Object> headers, Channel channel){
        System.out.println("******************开始接受消息***********************");
        System.out.println("接收到的消息："+msgInfo.toString());
        System.out.println("******************结束接受消息***********************");
    }

    /**
     * 引用的是rabbitmq官方的包
     * 可以自定义exchange和queue，并且会自动创建
     * @param args
     */
    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {

            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("39.107.87.42");
            connectionFactory.setUsername("root");
            connectionFactory.setPassword("root");
            connectionFactory.setVirtualHost("/");
            //创建连接
            Connection connection = connectionFactory.newConnection();
            //创建channel信道
            Channel channel = connection.createChannel();

            DefaultConsumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties,
                                           byte[] body) throws UnsupportedEncodingException {
                    System.out.println(new String(body, "UTF-8"));
                }
            };
            System.out.println("***************开始接收消息****************");
            channel.basicConsume("zxw-queue", true, UUID.randomUUID().toString(), consumer);
            System.out.println("***************结束接收消息****************");

        TimeUnit.SECONDS.sleep(4);
            //关闭资源
        channel.close();
        connection.close();


    }
}
