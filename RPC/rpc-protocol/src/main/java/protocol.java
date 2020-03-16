import com.damon.RpcRequest;

import java.net.URL;

public interface protocol {
    public void start(URL url);
    public Object send(URL url, RpcRequest invocation);
}
