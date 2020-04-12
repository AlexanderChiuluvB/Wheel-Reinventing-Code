package loadbalance.impl;

import com.damon.ServiceProvider;
import loadbalance.LoadStrategy;

import java.util.List;
import java.util.Random;

public class RandomLoadStrategy implements LoadStrategy {

    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        int size = serviceProviders.size();
        Random random = new Random();
        int idx = random.nextInt(size);
        return serviceProviders.get(idx);
    }
}
