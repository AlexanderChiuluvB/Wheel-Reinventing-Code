package protocol.loadbalance;

import protocol.loadbalance.impl.HashLoadStrategy;
import protocol.loadbalance.impl.PollingLoadStrategy;
import protocol.loadbalance.impl.RandomLoadStrategy;

import java.util.HashMap;
import java.util.Map;

public class LoadBalanceEngine {

    private static final Map<LoadBalanceStrategy, LoadStrategy>
        loadBalanceMap = new HashMap<LoadBalanceStrategy, LoadStrategy>();
    static {
        loadBalanceMap.put(LoadBalanceStrategy.Random, new RandomLoadStrategy());
        loadBalanceMap.put(LoadBalanceStrategy.Hash, new HashLoadStrategy());
        loadBalanceMap.put(LoadBalanceStrategy.Polling, new PollingLoadStrategy());

    }
    public static LoadStrategy queryLoadStrategy(String loadStrategy) {
        LoadBalanceStrategy loadBalanceStrategy = LoadBalanceStrategy.queryByCode(loadStrategy);
        if (loadBalanceStrategy == null) {
            return new RandomLoadStrategy();
        }
        return loadBalanceMap.get(loadBalanceStrategy);
    }

}
