import com.damon.Configuration;
import com.damon.URL;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import protocol.Protocol;
import protocol.socket.SocketProtocol;

public class Provider implements ApplicationContextAware {

    private ApplicationContext ctx;

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("rpc.xml");
        System.out.println(Configuration.getInstance().getAddress());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.ctx = ctx;

        URL url = new URL("localhost", 8080);
        //Register.register(url,HelloService.class.getName(),HelloServiceImpl.class.getName());

        //启动 Tomcat
        Protocol protocol = new SocketProtocol();
        protocol.start(url);

    }


}
