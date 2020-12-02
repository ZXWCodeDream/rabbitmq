package com.zxw.provider.source;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import entity.MsgInfo;
import org.springframework.stereotype.Component;

import java.io.IOException;
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
        channel.queueDeclare("zxw-queue",true,false,false,null);
        /**
         * exchange:交换机名
         * type:交换机的类型：topic direct fanout
         * duration:是否持久化
         * autodelete：是否自动删除
         * Map:设置一些其他参数
         */
        channel.exchangeDeclare("zxw-exchange","direct",true,false,null);
        /**
         * queue:
         * exchange:
         * routingkey:用于绑定队列和交换机的路由键
         */
        channel.queueBind("zxw-queue","zxw-exchange","zxw.bind");

        MsgInfo msgInfo = new MsgInfo("我是消息1","100001","12");
        /**
         * 发送消息
         * exchange：
         * routingKey
         * BasicProperties
         * byte[]
         */
        channel.basicPublish("zxw-exchange","zxw.bind",MessageProperties.PERSISTENT_TEXT_PLAIN ,msgInfo.toString().getBytes());

        //关闭资源
        channel.close();
        connection.close();

    }
}
