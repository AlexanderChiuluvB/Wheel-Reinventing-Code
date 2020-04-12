import com.damon.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ServerBeanDefinitionParser implements BeanDefinitionParser {
    private final Class<?> beanClass;

    public ServerBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Configuration.getInstance().setAddress(element.getAttribute("role"));
        return null;
    }
}
