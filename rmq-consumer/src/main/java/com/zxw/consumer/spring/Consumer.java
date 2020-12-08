package com.zxw.consumer.spring;

import com.rabbitmq.client.*;
import entity.MsgInfo;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    int time = 0;


//    可以在@Argument内添加参数包括x-message-ttl、x-dead-letter-exchange、x-dead-letter-routing-key等来定义queue
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "topic-queue",durable = "true",autoDelete = "false"
                    ,arguments = @Argument(name="x-message-ttl",value = "60000",type = "java.lang.Intger")),
            exchange = @Exchange(value = "topic-exchange",durable = "true",type = "topic"),
            key = "topic.consume"))
    @RabbitHandler
    public void received(@Payload MsgInfo msgInfo, @Headers Map<String,Object> headers, Channel channel) throws IOException {
        System.out.println("******************开始接受消息***********************");
        System.out.println("接收到的消息："+msgInfo.toString());
        System.out.println("******************结束接受消息***********************");

        long deliveryTag = (long)headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
//            int error = 1/0;
            //ACK手动签收，若不签收，队列中的消息会被消费，但仍存在队列中，标识为unacked,只要重启服务就会重新消费这条消息
            /**
             * 手动签收消息
             * deliveryTag:标识
             * multiple:是否允许批量签收
             */
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            //如果消息在手动签收之前报错，那么该消息就不会被签收，会滞留在队列中，只有重启服务中才可重新消费（但现实不会允许你在线上重启服务的）
            //那么有没有办法让未被签收的消息重新投递并消费呢？答案：NACK

            synchronized (this){
                time++;
            }

            //限制消息重回队列次数
            if (time <= 3) {
                System.out.println(time);
                /**
                 * 将消息重回队列，放在队尾中等待消费
                 * deliveryTag:标识
                 * multiple:是否允许批量签收
                 * requeue:是否重回队列
                 */
                channel.basicNack(deliveryTag, false, true);
            }else{
                time = 0;
                //NACK多次没成功后，将消息存放到DB、file、log中，待统一分析
                //手动签收这条消息
                channel.basicAck(deliveryTag, false);

            }
        }
    }

}
