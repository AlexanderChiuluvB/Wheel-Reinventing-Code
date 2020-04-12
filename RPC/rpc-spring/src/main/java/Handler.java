import com.damon.Configuration;
import com.damon.ServiceProvider;
import protocol.Protocol;
import protocol.dubbo.dubboProtocol;
import protocol.http.httpProtocol;
import protocol.socket.SocketProtocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class Handler<T> implements InvocationHandler {

    private Class<T> interfaceClass;

    Handler(Class<T> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Configuration configuration = Configuration.getInstance();

        Protocol protocol;

        if ("Dubbo".equalsIgnoreCase(configuration.getProcotol())) {
            protocol = new dubboProtocol();
        } else if ("Http".equalsIgnoreCase(configuration.getProcotol())) {
            protocol = new httpProtocol();
        } else if ("Socket".equalsIgnoreCase(configuration.getProcotol())) {
            protocol = new SocketProtocol();
        } else {
            protocol = new dubboProtocol();
        }

        String serviceKey = interfaceClass.getName();
        RegisterCenterForConsumer registerCenterForConsumer = ZookeeperRegisterCenter.getInstance();
        List<ServiceProvider> providerList = registerCenterForConsumer.getServiceMetaDataMapForConsumer().get(serviceKey);
        //根据负载均衡策略,选取服务提供者
        String strategy = configuration.getStragety();
        if (strategy == null || strategy.equalsIgnoreCase("")) {
            strategy = "random";
        }


    }


}
