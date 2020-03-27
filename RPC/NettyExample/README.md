### A simple Netty Client And Server

#### Server

* EchoServerHandler

1.For event-driven Netty, ChannelHandlers are invoked for different kinds of events.

2.Application implement or extend ChannelHandlers to hook into the event life-
                                                     cycle and provide custom application logic

3.Thus handler is used to decouple the network code and the custom code.


* bootstrap

1.Create a ServerBootstrap instance to start and bind the port.

2.Create and assign an NioEventLoopGroup instance to handle event processing new 
connections or data reading/writing.

3.For each connection, initialize a new Channel with the handler instance.

#### Client

* EchoServerHandler

channelRead0() — Called when a message is received from the server

* bootstrap

similar to Server's


### Key concept in Netty

* inbound 

events from server to client(read)

* outbound

events from client to server(written)

* Channel--Sockets

Channel表示一个链接,可以理解为每一个请求就是一个Channel

* EventLoop--Control flow,multithreading,concurrency

1.EventLoopGroup 包含多个EventLoop

2.每一个EventLoop会和一个线程绑定

3.所有由EventLoop处理的IO事件都会由其绑定的线程处理

4.一个Channel会注册到一个EventLoop

5.一个EventLoop可以关联多个Channels

"I/O for a given Channel is executed by the same
 Thread , virtually eliminates the need for synchronization."

一个Channel的IO操作是由单个线程负责的,省去了同步操作的开销

* ChannelFuture--Asynchronous notification

Netty中所有IO操作都是异步的,意味着函数调用的结果不会即使返回.
ChannelFuture可以理解为一个操作的结果的占位符,由于操作不一定立即执行,
但是能保证一定会执行.因此就用Future来保存一个"未返回结果"

具体来说,ChannelFuture的addListener()方法会注册一个ChannelFutureListener,
就一个监听器,当操作结束之后就通知这个Listener

### ChannelHandler 和 ChannelPipeline

* ChannelHandler

负责具体业务逻辑代码,负责流入流出的数据流流动

每一个事件对应着一个ChannelHandler

* ChannelPipeline

Provide a chain of ChannelHandlers and defines

an API for propagating the flow of inbound and outbound events

Each Channel will be assigned to a ChannelPipeline

1. A ChannelInitializer implementation is registered with a ServerBootstrap.

2. When ChannelInitializer.initChannel() is called, the Channel Initializer will install

a custom set of ChannelHandlers in the pipeline.

When a ChannelHandler is added to a ChannelPipeline, it's assigned to a ChannelHandler

Context Object, which represents the binding between a ChannelHandler and the pipeline.

So there are two ways of sending messages in Netty:

1.Write to Channel directly

2.Write to a ChannelHandlerContext

### Encoders and Decoders

Outbound: client->server use encoders

Inbound: server->client use decoders

### Bootstraps

* Bootstrap(Client side)

1.Bind to a host and a port.

2.Use only one EventLoopGroup

* ServerBootstrap(Server side)

1.Bind to a port.

2.Require two EventLoopGroup

![1585276830230](RPC/NettyExample/1585276830230.png)



