package com.zxw.provider.source;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * ClassName: TransactionProvider
 * Description:
 * 生产端生产确认
 *  支持事务确认
 *  rabbitmq事务确认影响性能，严重影响队列吞吐量,不建议使用
 * @author zxw
 * @date 2020/12/8 7:56 下午
 * @since JDK 1.8
 */
public class TransactionProvider {

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

        channel.queueDeclare("transaction-queue",true,false,false,null);
        channel.exchangeDeclare("transaction-exchange","topic",true,false,null);
        channel.queueBind("transaction-queue","transaction-exchange","transaction.#");
        //开启事务
        channel.txSelect();
        for (int i = 0; i < 10; i++) {
            try {
                String message = "我是第" + i + "条消息";
                channel.basicPublish("transaction-exchange", "transaction.insert", MessageProperties.TEXT_PLAIN, message.getBytes());
                //发送完毕之后出现问题，回滚操作会让队列中的消息回退
                if(i == 5){
                    i = i/0;
                }
                //事务确认
                channel.txCommit();
            }catch (Exception e){
                System.out.println("有错误发生，回滚消息");
                //事务回滚
                channel.txRollback();
                //重发操作
            }
        }




    }
}
