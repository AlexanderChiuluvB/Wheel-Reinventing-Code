package protocol.loadbalance.impl;

import com.damon.ServiceProvider;
import protocol.loadbalance.LoadStrategy;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PollingLoadStrategy implements LoadStrategy {

    private int index;
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        ServiceProvider serviceProvider = null;
        try {
            reentrantLock.tryLock(10, TimeUnit.MILLISECONDS);
            if (index >= serviceProviders.size()) {
                index = 0;
            }
            serviceProvider = serviceProviders.get(index++);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            reentrantLock.unlock();
        }
        if (serviceProvider == null) {
            serviceProvider = serviceProviders.get(0);
        }
        return serviceProvider;
    }
}
