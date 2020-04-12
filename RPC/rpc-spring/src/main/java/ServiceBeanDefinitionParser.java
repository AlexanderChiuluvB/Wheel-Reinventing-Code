import com.damon.Configuration;
import com.damon.ServiceConsumer;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class ServiceBeanDefinitionParser implements BeanDefinitionParser {
    private final Class<?> beanClass;

    public ServiceBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String interfaces = element.getAttribute("interfaces");
        String ref = element.getAttribute("ref");
        Class clazz = null;
        try {
            clazz = Class.forName(interfaces);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();

        definition.getConstructorArgumentValues().addGenericArgumentValue(clazz);
        definition.setBeanClass(ProxyFactory.class);
        definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);

        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        beanDefinitionRegistry.registerBeanDefinition(ref, definition);

        ZookeeperRegisterCenter zookeeperRegisterCenter = ZookeeperRegisterCenter.getInstance();

        ServiceConsumer invoker = new ServiceConsumer();
        List<ServiceConsumer> consumerList = new ArrayList<ServiceConsumer>();
        invoker.setConsumer(clazz);
        invoker.setServiceObject(interfaces);
        invoker.setGroupName("");
        consumerList.add(invoker);

        return definition;
    }
}
