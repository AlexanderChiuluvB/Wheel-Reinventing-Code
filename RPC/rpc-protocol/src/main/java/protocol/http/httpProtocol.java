package protocol.http;

import com.damon.RpcRequest;
import com.damon.URL;
import protocol.Protocol;


public class httpProtocol implements Protocol {

    @Override
    public void start(URL url) {
        httpServer httpServer = protocol.http.httpServer.getInstance();
        assert httpServer != null;
        httpServer.start(url.getHost(), url.getPort());

    }

    @Override
    public Object send(URL url, RpcRequest invocation) {
        httpClient httpClient = protocol.http.httpClient.getInstance();
        return httpClient.post(url.getHost(), url.getPort(), invocation);
    }

}
