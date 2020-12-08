package com.zxw.consumer.source;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: RpcServer
 * Description:
 * 负责接收到消息，并且将接受的消息进行计算，最终发送到回调队列
 * @author zxw
 * @date 2020/12/4 4:00 下午
 * @since JDK 1.8
 */
public class RpcServer {

    public static void main(String[] args) throws IOException, TimeoutException {

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
                                       byte[] body) throws IOException {

                AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().correlationId(properties.getCorrelationId()).build();
                //接受消息
                String message = new String(body,"utf-8");
                System.out.println("接受到的消息:"+message);
                //计算消息
                String response = String.valueOf(fib(Integer.valueOf(message)));
                System.out.println("计算结果:"+response);
                //发送消息
                channel.basicPublish("",properties.getReplyTo(),basicProperties,response.getBytes());
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        };
        channel.basicConsume("rpc-queue",false,consumer);
    }

    public static int fib(int n ){
        if(n == 0)return 0;
        if (n == 1)return 1;
        return fib(n-1)+fib(n-2);
    }
}
