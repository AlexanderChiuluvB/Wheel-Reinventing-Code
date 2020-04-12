import com.damon.Configuration;
import com.damon.ServiceProvider;
import com.damon.URL;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import protocol.Protocol;
import protocol.dubbo.channelPool.NettyChannelPoolFactory;
import protocol.dubbo.dubboProtocol;
import protocol.http.httpProtocol;
import protocol.socket.SocketProtocol;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

public class ProtocolBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public ProtocolBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String protocolType = element.getAttribute("protocol");
        int port = Integer.parseInt(element.getAttribute("port"));
        Configuration.getInstance().setProcotol(protocolType);
        Configuration.getInstance().setPort(port);
        Configuration.getInstance().setSerialize(element.getAttribute("serialize"));
        Configuration.getInstance().setStragety(element.getAttribute("stragety"));
        Configuration.getInstance().setRole(element.getAttribute("role"));
        Configuration.getInstance().setAddress(element.getAttribute("address"));
        if (element.getAttribute("role").equalsIgnoreCase("provider")) {

            Protocol protocol = null;
            if (protocolType.equalsIgnoreCase("Dubbo")) {
                protocol = new dubboProtocol();
            } else if (protocolType.equalsIgnoreCase("Http")) {
                protocol = new httpProtocol();
            } else if (protocolType.equalsIgnoreCase("socket")) {
                protocol = new SocketProtocol();
            } else {
                protocol = new dubboProtocol();
            }

            try {
                InetAddress addr = InetAddress.getLocalHost();
                String ip = addr.getHostAddress();
                if(port == 0){
                    port = 32115;
                }
                URL url = new URL(ip, port);
                protocol.start(url);

            } catch (Exception e){
                e.printStackTrace();
            }
            //consumer
        } else{
            //获取服务注册中心
            ZookeeperRegisterCenter zookeeperRegisterCenter = ZookeeperRegisterCenter.getInstance();

            //初始化服务提供者列表到本地缓存
            zookeeperRegisterCenter.initProviderMap();

            //初始化Channel
            Map<String, List<ServiceProvider>> providerMap = zookeeperRegisterCenter.getServiceMetaDataMapForConsumer();
            if(MapUtils.isEmpty(providerMap)) {
                throw new RuntimeException("ServiceProviderList empty");
            }
            NettyChannelPoolFactory.getInstance().initNettyChannelPoolFactory(providerMap);
        }
        return null;

    }
}
