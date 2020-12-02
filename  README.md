# RabbitMQ学习之路

项目分为三个模块:

**Rmq-basic**: 公共模块

**Rmq-consumer**：消费者模块

**Rmq-provider**： 生产者模块

### 基础知识

**AMQP名词介绍**

- server:也可以称作broker，可以简单看做是一个rabbitmq服务节点或者rabbit服务实例
- Producer: 生产者，负责发送消息
- Consumer:消费者，负责接收消息
- Exchange: 交换器，生产者会把消息首先发送到交换器，由交换器进行路由转发到对应的队列中
- Queue:消息队列，存放消息的地方
- RoutingKey:路由键，绑定交换器和队列
- Bingding: 绑定，交换器和队列之间的虚拟连接，binding中包含routingkey
- Connection：连接
- Channel：信道，每一个channel代表一个会话
- Message:消息体
- Virtual host: 虚拟主机，可以理解为一个工作空间或者文件夹，进行逻辑分离
  - 一个VH下可以存在多个exchange和queue
  - 一个VH下不可以存在相同名字的exchange和queue

 **rabbitmq消息流转过程**

- producer将消息发送给指定的exchange
- exchange通过routingkey路由规则分发消息到指定的queue
- consumer监听指定的queue消费消息

  生产者和消费者是解耦的，它们通过routingkey关联。