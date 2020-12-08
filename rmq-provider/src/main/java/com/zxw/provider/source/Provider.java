package com.zxw.provider.source;

import com.rabbitmq.client.*;
import entity.MsgInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: Provider
 * Description:
 *
 * 原生方法发送消息
 * @author zxw
 * @date 2020/12/1 8:23 下午
 * @since JDK 1.8
 */
public class Provider {

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
        /**
         * queue:队列名
         * durable：是否持久化
         * exclusive：是否排他的，若设置为true，则意味这个队列只对首次对它的连接可见
         * autodelete：是否自动删除的
         * Map:设置一些其他参数，譬如x-message-ttl
         */
        Map<String,Object> map = new HashMap<>();
        map.put("x-message-ttl",60000); // 设置队列中统一消息过期时间为60s
        map.put("x-dead-letter-exchange","dead-exchange"); // 绑定死信消息和交换器
        map.put("x-dead-letter-routing-key","dead.flow");
        map.put("x-max-priority",10);
        channel.queueDeclare("source-queue",true,false,false,map);
        /**
         * exchange:交换机名
         * type:交换机的类型：topic direct fanout
         * duration:是否持久化
         * autodelete：是否自动删除
         * Map:设置一些其他参数
         */
        channel.exchangeDeclare("source-exchange","topic",true,false,null);
        /**
         * queue:
         * exchange:
         * routingkey:用于绑定队列和交换机的路由键
         */
        channel.queueBind("source-queue","source-exchange","source.*");

        MsgInfo msgInfo = new MsgInfo("我是会过期的消息","100001","12");

//        默认基础文本配置
        BasicProperties defaultBasicProperties = MessageProperties.PERSISTENT_TEXT_PLAIN;
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.contentType("text/plain");
        builder.deliveryMode(2); //持久化消息
        builder.expiration("5000");//该条消息设置ttl=5s
        builder.priority(5); // 设置优先级为5
        AMQP.BasicProperties basicProperties = builder.build();
        /**
         * 发送消息
         * exchange：
         * routingKey
         * BasicProperties:基础配置，可以通过建筑者模式构建基础数据
         * byte[]
         */
        channel.basicPublish("source-exchange","source.bind",basicProperties ,msgInfo.toString().getBytes());

        //关闭资源
        channel.close();
        connection.close();

    }
}
