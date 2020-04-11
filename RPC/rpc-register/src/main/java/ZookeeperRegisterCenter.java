import com.damon.Configuration;
import com.damon.ServiceConsumer;
import com.damon.ServiceProvider;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.common.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分别给服务提供方和消费方在Zk注册临时节点
 *
 * 然后使用Zk的监听机制定期更新ServiceProvider的信息
 *
 */
public class ZookeeperRegisterCenter implements RegisterCenterForConsumer, RegisterCenterForProvider {

    private static ZookeeperRegisterCenter zookeeperRegisterCenter = new ZookeeperRegisterCenter();

    private ZookeeperRegisterCenter() {
    }

    public static ZookeeperRegisterCenter getInstance() {
        return zookeeperRegisterCenter;
    }

    //服务提供者的列表 key: 服务提供者接口 value:服务提供者服务方法列表
    private static final Map<String, List<ServiceProvider>> providerServiceMap = new ConcurrentHashMap();

    //服务端zookeeper元信息
    private static final Map<String, List<ServiceProvider>> serviceDataForConsumer = new ConcurrentHashMap();

    // 从配置文件获取zk服务地址列表
    private static String ZK_SERVICE = Configuration.getInstance().getAddress();

    //从配置文件中获取 zookeeper 会话超时时间配置
    private static int ZK_SESSION_TIME_OUT = 5000;

    //从配置文件中获取 zookeeper 连接超时事件配置
    private static int ZK_CONNECTION_TIME_OUT = 5000;

    //Zk 注册路径
    private static String ROOT_PATH = "/rpc_register";
    public static String PROVIDER_TYPE = "/provider";
    public static String CONSUMER_TYPE = "/consumer";

    private static volatile ZkClient zkClient = null;

    public void initProviderMap() {
        if (serviceDataForConsumer.isEmpty()) {
            serviceDataForConsumer.putAll(FetchMetaData());
        }
    }

    /**
     * 从Zookeeper获得服务提供者的列表
     *
     * @return
     */
    private Map<String, List<ServiceProvider>> FetchMetaData() {
        final Map<String, List<ServiceProvider>> providerServiceMap = new ConcurrentHashMap();
        synchronized (ZookeeperRegisterCenter.class) {

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT,
                        new SerializableSerializer());
            }

            String providerPath = ROOT_PATH + PROVIDER_TYPE;
            List<String> providerServices = zkClient.getChildren(providerPath);
            for (String serviceName : providerServices) {
                String servicePath = providerPath + "/" + serviceName;
                List<String> ipPathList = zkClient.getChildren(servicePath);
                for (String ip : ipPathList) {
                    String serverIp = ip.split("\\|")[0];
                    String serverPort = ip.split("\\|")[1];
                    String Impl = ip.split("\\|")[2];
                    List<ServiceProvider> providerList = providerServiceMap.get(serviceName);
                    if (providerList == null) {
                        providerList = new ArrayList<ServiceProvider>();
                    }
                    ServiceProvider providerService = new ServiceProvider();

                    try {
                        Class clazz = Class.forName(serviceName);
                        providerService.setProvider(clazz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    providerService.setIp(serverIp);
                    providerService.setPort(Integer.parseInt(serverPort));
                    providerService.setServiceObject(Impl);
                    providerService.setGroupName("");
                    providerList.add(providerService);

                    providerServiceMap.put(serviceName, providerList);

                }

                //Zk 监听注册服务的变化,同时更新数据到本地缓存
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {

                        if (currentChilds == null) {
                            currentChilds = new ArrayList<String>();
                        }
                        List<String> activeServiceIpList = new ArrayList<String>();
                        for (String input : currentChilds) {
                            String ip = StringUtils.split(input, "|").get(0);
                            activeServiceIpList.add(ip);
                        }
                        refreshServiceMetaDataMap(activeServiceIpList);
                    }
                });
            }
        }
        return providerServiceMap;
    }

    /**
     * 由于serviceDataForConsumer 第一次是由zk拉取的,后续是由zk监听主动更新到缓存
     *
     * @param activeServiceIpList
     */
    private void refreshServiceMetaDataMap(List<String> activeServiceIpList) {

        if (activeServiceIpList == null) {
            activeServiceIpList = new ArrayList<String>();
        }
        // 最后汇总的更新后的Map
        Map<String, List<ServiceProvider>> currentServiceMap = new ConcurrentHashMap();
        for (Map.Entry<String, List<ServiceProvider>> entry : serviceDataForConsumer.entrySet()) {
            String serviceKey = entry.getKey();
            // 这个Key对应的ServiceProvider的List
            List<ServiceProvider> serviceProviderList = entry.getValue();
            // 这个Key汇总更新后的List
            List<ServiceProvider> UpdatedProviderList = currentServiceMap.get(serviceKey);
            if (UpdatedProviderList == null) {
                UpdatedProviderList = new ArrayList<ServiceProvider>();
            }

            for (ServiceProvider serviceProvider : serviceProviderList) {
                if (activeServiceIpList.contains(serviceProvider.getIp())) {
                    UpdatedProviderList.add(serviceProvider);
                }
            }

            currentServiceMap.put(serviceKey, UpdatedProviderList);

        }
        serviceDataForConsumer.clear();
        serviceDataForConsumer.putAll(currentServiceMap);
    }

    /**
     * 给消费端返回暴露的服务端
     *
     * @return
     */
    public Map<String, List<ServiceProvider>> getServiceMetaDataMapForConsumer() {
        return serviceDataForConsumer;
    }

    public void registerConsumer(List<ServiceConsumer> consumers) {
        if (consumers == null || consumers.size() == 0) {
            return;
        }
        synchronized (ZookeeperRegisterCenter.class) {
            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }

            // 创建zk命名空间
            boolean exist = zkClient.exists(ROOT_PATH);
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH, true);
            }

            //创建服务提供者节点
            exist = zkClient.exists((ROOT_PATH));
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH);
            }

            for (int i = 0; i < consumers.size(); i++) {
                ServiceConsumer serviceConsumer = consumers.get(i);
                String consumerName = serviceConsumer.getConsumer().getName();
                String consumerPath = ROOT_PATH + CONSUMER_TYPE + "/" + consumerName;

                if (!zkClient.exists(consumerPath)) {
                    zkClient.createPersistent(consumerPath, true);
                }

                // 创建当前服务器的临时节点
                InetAddress addr = null;
                try {
                    addr = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ip = addr.getHostAddress();
                String currentServiceIpNode = consumerPath + "/" + ip;
                if (!zkClient.exists(currentServiceIpNode)) {
                    zkClient.createEphemeral(currentServiceIpNode);
                }
            }
        }
    }

    /**
     * 在某个服务端调用自己暴露的服务
     *
     * @return
     */
    public Map<String, List<ServiceProvider>> getServiceMetaDataMapForProvider() {
        return providerServiceMap;
    }

    public void registerProvider(List<ServiceProvider> providers) {
        if (providers == null || providers.size() == 0) {
            return;
        }
        synchronized (ZookeeperRegisterCenter.class) {
            for (ServiceProvider provider : providers) {
                String key = provider.getProvider().getName();
                //先从当前服务提供者的集合里面提取
                List<ServiceProvider> currentList = providerServiceMap.get(key);
                if (currentList == null) {
                    currentList = new ArrayList();
                }
                currentList.add(provider);
                providerServiceMap.put(key, currentList);
            }

            if (zkClient == null) {
                zkClient = new ZkClient(ZK_SERVICE, ZK_SESSION_TIME_OUT, ZK_CONNECTION_TIME_OUT, new SerializableSerializer());
            }

            //ZOOKEEPER命名空间
            boolean exist = zkClient.exists(ROOT_PATH);
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH, true);
            }

            exist = zkClient.exists((ROOT_PATH));
            if (!exist) {
                zkClient.createPersistent(ROOT_PATH);
            }

            for (Map.Entry<String, List<ServiceProvider>> entry : providerServiceMap.entrySet()) {
                String serviceNode = entry.getKey();
                String servicePath = ROOT_PATH + PROVIDER_TYPE + "/" + serviceNode;
                if (!zkClient.exists(servicePath)) {
                    zkClient.createPersistent(servicePath, true);
                }

                InetAddress addr = null;
                try {
                    addr = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ip = addr.getHostAddress();
                int serverPort = entry.getValue().get(0).getPort();
                String impl = (String) entry.getValue().get(0).getServiceObject();
                String serviceIpNode = servicePath + "/" + ip + "|" + serverPort + "|" + impl;
                if (!zkClient.exists(serviceIpNode)) {
                    zkClient.createEphemeral(serviceIpNode);
                }
                zkClient.subscribeChildChanges(servicePath, new IZkChildListener() {
                    public void handleChildChange(String s, List<String> list) throws Exception {
                        if (list == null) {
                            list = new ArrayList<String>();
                        }
                        //存活的ipList
                        List<String> activeServiceIpList = new ArrayList<String>();
                        for (String input : list) {
                            String ip = StringUtils.split(input, "|").get(0);
                            activeServiceIpList.add(ip);
                        }
                        refreshServiceMetaDataMap(activeServiceIpList);
                    }
                });
            }
        }
    }
}
