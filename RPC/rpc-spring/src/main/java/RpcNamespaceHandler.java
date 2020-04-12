import com.damon.Configuration;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("protocol", new ProtocolBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("register", new RegisterBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("application", new ApplicationBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("provider", new ProviderBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("role", new ServerBeanDefinitionParser(Configuration.class));
        registerBeanDefinitionParser("service", new ServiceBeanDefinitionParser(Configuration.class));
    }



}
