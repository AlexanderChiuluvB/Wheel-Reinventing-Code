package protocol;


import com.damon.RpcRequest;
import com.damon.URL;


public interface Protocol {

    void start(URL url);
    Object send(URL url, RpcRequest invocation);
}
