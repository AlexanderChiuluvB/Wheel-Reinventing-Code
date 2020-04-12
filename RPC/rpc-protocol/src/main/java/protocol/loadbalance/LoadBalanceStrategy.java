package protocol.loadbalance;

public enum LoadBalanceStrategy {

    Random("Random"),

    WeightRandom("WeightRandom"),

    Polling("Polling"),

    WeightPolling("WeightPolling"),

    SmoothWeightPolling("SmoothWeightPolling"),

    Hash("Hash"),

    ConsistentHash("ConsistentHash");


    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    LoadBalanceStrategy(String desc) {
        this.desc = desc;
    }

    public static LoadBalanceStrategy queryByCode(String desc) {

        if (null == desc || "".equalsIgnoreCase(desc)) {
            return null;
        }
        for (LoadBalanceStrategy strategy : values()) {
            if (strategy.getDesc().equalsIgnoreCase(desc)) {
                return strategy;
            }
        }
        return null;
    }

}
