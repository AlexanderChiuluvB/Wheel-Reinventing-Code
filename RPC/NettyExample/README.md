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
