package com.zxw.consumer.source;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: Consumer
 * Description:
 *
 * 使用原生语法实现消息消费
 * @author zxw
 * @date 2020/12/1 8:42 下午
 * @since JDK 1.8
 */

public class Consumer {

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
