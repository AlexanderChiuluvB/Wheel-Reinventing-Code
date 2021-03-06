package protocol.socket;

import com.damon.RpcRequest;
import com.damon.URL;
import protocol.Protocol;


public class SocketProtocol implements Protocol {

    public void start(URL url) {
        SocketServer socketServer = SocketServer.getInstance();
        assert socketServer != null;
        socketServer.publish(url.getPort());
    }

    public Object send(URL url, RpcRequest invocation) {
        SocketClient socketClient = SocketClient.getInstance();
        return socketClient.sendRequest(url.getHost(), url.getPort(), invocation);
    }


}
