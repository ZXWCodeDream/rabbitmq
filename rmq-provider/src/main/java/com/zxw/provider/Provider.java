package com.zxw.provider;

import com.rabbitmq.client.*;
import entity.MsgInfo;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: Provider
 * Description:
 *
 * @author zxw
 * @date 2020/12/1 8:23 下午
 * @since JDK 1.8
 */
@Component
public class Provider {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 特别要注意 rabbitTemplate不会自动创建queue和exchange，需要手动创建才会成功发送消息
     * @param msgInfo
     */
    public void send(MsgInfo msgInfo){

        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(msgInfo.getMsgId());
        /**
         * exchange: 交换机名称
         * routingKey:与队列绑定的关联key
         * object:要传送的消息
         * correlationData：消息的唯一ID
         */
        rabbitTemplate.convertAndSend("msg-exchange","msg.update",msgInfo,correlationData);
    }

    /**
     * 这是引用的rabbitmq官方默认的包
     * 会自动创建exchange以及队列关系
     * @param args
     */
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
