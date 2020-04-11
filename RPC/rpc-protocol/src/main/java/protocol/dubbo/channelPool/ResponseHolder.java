package protocol.dubbo.channelPool;

import com.damon.MessageCallBack;

import java.util.concurrent.ConcurrentHashMap;

public class ResponseHolder {

    private static ResponseHolder INSTANCE = new ResponseHolder();

    private ResponseHolder(){}

    public static ResponseHolder getInstance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<String, MessageCallBack> callBackConcurrentHashMap
     = new ConcurrentHashMap<>();

}
