package protocol;


import com.damon.RpcRequest;

import java.net.URL;

public interface Procotol {

    void start(URL url);
    Object send(URL url, RpcRequest invocation);
}
