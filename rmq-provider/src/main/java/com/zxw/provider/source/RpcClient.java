package com.zxw.provider.source;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: RpcClient
 * Description:
 *
 * 概念：
 * RPC : remote procedure call.远程过程调用。它是一种通过网络从远程计算机上请求服务
 * 通俗来讲： 存在服务器A和服务器B，一个应用部署在服务器A ,一个应用部署在服务器B。服务器A的应用想要调取服务器B的应用的接口是无法实现的，因为不在一个内存空间，
 * 需要通过网络来表达调用的语义和传达调用的数据。
 *
 * RpcClient功能:
 * 发送消息（一个整数）到队列中，等待返回计算费波那西数列结果
 * @author zxw
 * @date 2020/12/4 4:01 下午
 * @since JDK 1.8
 */
public class RpcClient {

    private Connection connection;
    private Channel channel;

    //回调队列名
    private String replyQueueName;
    //消费监听
    private DefaultConsumer repleyConsume;
    //消息唯一ID
    private String correlationId;

   public RpcClient() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("39.107.87.42");
        connectionFactory.setUsername("root");
        connectionFactory.setPassword("root");
        connectionFactory.setVirtualHost("/");
        //创建连接
         connection = connectionFactory.newConnection();
        //创建channel信道
         channel = connection.createChannel();

       channel.queueDeclare("rpc-queue",true,false,false,null);
       channel.exchangeDeclare("rpc-exchange","topic",true,false,null);
       channel.queueBind("rpc-queue","rpc-exchange","rpc.*");

       // 使用系统自定义的队列名，只要使用RPCClient，那么所有回调消息都将从该队列传输，构造函数只创建一次
        replyQueueName = channel.queueDeclare().getQueue();
        repleyConsume = new DefaultConsumer(channel){
           @Override
           public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
               if (properties.getCorrelationId().equals(correlationId)){
                   System.out.println("返回的计算结果消息为:"+new String(body,"UTF-8"));
               }
           }

       };
        //持续订阅该队列
        channel.basicConsume(replyQueueName,repleyConsume);

    }

    public void call(String message) throws IOException {
       correlationId = UUID.randomUUID().toString();
        /**
         * replyTo:设置回调队列
         * correlationId:用来关联请求（request）和其调用RPC之后的回复（response）
         */
        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder().correlationId(correlationId).replyTo(replyQueueName).build();
       channel.basicPublish("rpc-exchange","rpc.insert",basicProperties,message.getBytes());
    }

    public void close() throws IOException, TimeoutException {
       channel.close();
       connection.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        RpcClient rpcClient = new RpcClient();
        rpcClient.call("10");
        System.out.println("发送消息成功");
        TimeUnit.MINUTES.sleep(10);
        rpcClient.close();
    }

}
