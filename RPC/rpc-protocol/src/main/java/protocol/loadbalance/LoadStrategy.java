package protocol.loadbalance;

import com.damon.ServiceProvider;

import java.util.List;

public interface LoadStrategy {
    public ServiceProvider select(List<ServiceProvider> serviceProviders);
}
