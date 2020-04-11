import com.damon.ServiceConsumer;
import com.damon.ServiceProvider;

import java.util.List;
import java.util.Map;

public interface RegisterCenterForConsumer {

    /**
     * 　消费端初始化服务提供者信息的本地缓存
     */
    public void initProviderMap();

    /**
     * 消费端获取服务提供消息
     *
     * @return
     */
    public Map<String, List<ServiceProvider>> getServiceMetaDataMapForConsumer();

    /**
     * 消费端把消费者注册到对应节点下
     *
     * @param invokers
     */
    public void registerConsumer(final List<ServiceConsumer> invokers);

}
