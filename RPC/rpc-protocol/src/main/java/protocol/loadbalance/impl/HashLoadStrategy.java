package loadbalance.impl;

import com.damon.ServiceProvider;
import loadbalance.LoadStrategy;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class HashLoadStrategy implements LoadStrategy {
    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {

        int size = serviceProviders.size();
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        String ip = addr.getHostAddress();
        return serviceProviders.get(ip.hashCode() % size);
    }
}
