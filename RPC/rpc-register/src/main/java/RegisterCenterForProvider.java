import com.damon.ServiceProvider;

import java.util.List;
import java.util.Map;

public interface RegisterCenterForProvider {

    /**
     * 消费端获取服务提供消息
     *
     * @return
     */
    public Map<String, List<ServiceProvider>> getServiceMetaDataMapForProvider();

    /**
     * 服务端把服务提供者注册到zk对应节点下
     *
     * @param invokers
     */
    public void registerProvider(final List<ServiceProvider> invokers);

}
