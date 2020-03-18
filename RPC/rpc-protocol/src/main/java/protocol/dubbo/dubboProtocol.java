package protocol.dubbo;

import com.damon.RpcRequest;
import com.damon.URL;
import protocol.Procotol;

import java.nio.channels.Channel;
import java.util.concurrent.ArrayBlockingQueue;

public class dubboProtocol implements Procotol {

    @Override
    public void start(URL url) {
        nettyServer server = nettyServer.getInstance();
        server.start(url.getHost(), url.getPort());
    }

    @Override
    public Object send(URL url, RpcRequest invocation) {
        ArrayBlockingQueue<Channel> queue =



    }
}
