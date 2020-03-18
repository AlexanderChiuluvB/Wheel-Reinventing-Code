package protocol;


import com.damon.RpcRequest;
import com.damon.URL;


public interface Procotol {

    void start(URL url);
    Object send(URL url, RpcRequest invocation);
}
