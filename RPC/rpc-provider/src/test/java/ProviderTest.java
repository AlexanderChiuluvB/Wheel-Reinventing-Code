import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class ProviderTest {

    @Test
    public void testIp() throws UnknownHostException {
        InetAddress addr = InetAddress.getLocalHost();
        System.out.println(addr.getHostAddress());
    }

}