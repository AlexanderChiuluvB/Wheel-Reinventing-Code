### 造轮子的代码

#### RPC

正在施工中,Netty-Example这个module包含了<<Netty In Action>>的代码和笔记

参考 https://gitbook.cn/books/5d18c490ba5e347f1f839844/index.html

从头写一个RPC框架(网络协议分别基于socket,http和Netty)


#### Kappa

Kafka+Flink1.10+Mysql 模拟一个简单的流批处理统一的Kappa 架构

Flink1.10 merge了 Blink planner, 把流批处理统一到同一逻辑代码.


#### producerConsumer

分别用BlockingQueue,ReentrantLock,Semaphore,notify/signal来手写生产者消费者模型demo
