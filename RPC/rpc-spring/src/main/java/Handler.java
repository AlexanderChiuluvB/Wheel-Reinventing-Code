import com.damon.Configuration;
import com.damon.RpcRequest;
import com.damon.ServiceProvider;
import com.damon.URL;
import loadbalance.LoadBalanceEngine;
import loadbalance.LoadStrategy;
import protocol.Protocol;
import protocol.dubbo.dubboProtocol;
import protocol.http.httpProtocol;
import protocol.socket.SocketProtocol;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

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
        LoadStrategy loadStrategyService = LoadBalanceEngine.queryLoadStrategy(strategy);
        ServiceProvider serviceProvider = loadStrategyService.select(providerList);
        URL url = new URL(serviceProvider.getIp(), serviceProvider.getPort());
        String impl = serviceProvider.getServiceObject().toString();
        int timeout = 2000;
        RpcRequest invocation = new RpcRequest(UUID.randomUUID().toString(),
                interfaceClass.getName(),
                method.getName(),
                args,
                method.getParameterTypes(), impl, timeout);
        return protocol.send(url, invocation);
    }
}
