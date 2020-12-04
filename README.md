# RabbitMQ学习之路

项目分为三个模块:

**Rmq-basic**: 公共模块

**Rmq-consumer**：消费者模块

**Rmq-provider**： 生产者模块


#### rabbitmq介绍

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



#### ACK和NACK

- **ACK**就是手动签收的标识，消息成功消费后，代码中进行ACK签收，则消息将从队列中移出，若未签收，消息则滞留在队列中，标识为unacked,滞留消息不会重新被消费，只有当服务重启时才能被消费。

- **NACK**是将unacked的消息重回队列，并放入队尾。在签收消息之前可能会出现异常导致未手动签收，那么就需要在异常处使用NACK让消息重回队列重新被消费，一般而言，我们会设置重新被消费的次数（使用redis计数），若在指定次数内都不能将该消息签收，则将该消息存入db或者log或者file中，后续用作分析使用。

  > 代码见com.zxw.consumer.spring.Consumer

#### Exchange交换器type详解

- **direct**：点对点直连概念，比如我们在bindings中指定routingkey为msg.update,那么发送消息到队列指定的routingKey则必须为msg.update
- **topic**: 点对点直连概念，相比direct，topic支持routingkey模糊匹配，可以在routingKey写匹配符
  - *:表示一个单词
  - #：表示没有或多个单词
- **fanout**：广播模式，只有exchange绑定了queue，就都可以匹配，跟routingkey配置无关。无需借助路由，所以发送消息到队列最快
- **headers**:使用较少

#### 创建队列参数介绍
**什么是TTL？time to live:存活时间**
**创建队列可选参数**
- **x-message-ttl**:队列内的消息在没被消费时的存活时间
- **x-expires**:若消息队列在空闲状态的时间超过该参数设置的值，该队列将被删除
  - 空闲状态：**没有消费者在消费**，生产者发送消息是发送到exchange，再由exchange路由到queue,此时queue也属于空闲状态（亲自实验结果：一直向队列发送消息，队列也会在指定时间内删除）
- **x-max-length**:消息存放消息队列的数量，若超
- 过限制，则丢弃最早的消息
- **x-max-length-bytes**:消息队列的最大容量，新消息过来如果容量不够会删除最早的消息，如果还不够，再删一条次最早的消息

#### 死信队列

**什么叫死信？**在**未被消费掉之前就失效**的消息成为死信

**什么叫死信队列？** 死信队列其实就是个普通队列，不过专门接受死信消息。

那么死信队列有啥用呢？做**延迟消息发送**


**死信队列实现：**

- 创建队列dead-queue当做死信队列
- 创建交换器dead-exchange作死信消息转发,routingKey为dead.*绑定dead-queue
- 创建队列topic-queue,设置参数x-dead-letter-exchange=dead-exchange,x-dead-letter-routing-key=dead.key表示该队列的死信消息将发送到dead-exchange交换器上，最终根据dead-exchange绑定dead-queue,则死信消息流转到dead-queue
- 创建交换器topic-exchange,设定路由routingkey=topic.*绑定topic-queue

 **延迟消息流转:**

 设定消息过期时间t->发送消息到topic-exchange->路由转发到topic-queue->消息停留t时间段后->消息变为死信消息转发到dead-exchange->路由转发到dead-queue