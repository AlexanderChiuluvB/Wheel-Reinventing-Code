package common;

public interface ServiceInterface {
    void start();

    void stop();

    void waitToShutDown() throws InterruptedException;

    String getName();

    ServiceStatus getStatus();

}
