package common;

import java.util.List;

public abstract class AbstractStreamService<I,O> implements ServiceInterface {

    abstract protected void beforeStart() throws Exception;
    abstract protected void beforeEnd() throws Exception;

    abstract protected List<I> poll(List<Queue<I>> inputQueues) throws Exception;

    abstract protected List<O> process(List<I> inputs) throws Exception;

    abstract protected boolean offer(List<Queue<O>> outputQueues, List<O> outputs) throws Exception;

    private List<ServiceInterface> upstreams;
    private List<Queue<I>> inputQueues;
    private List<Queue<O>> outputQueues;

    private Thread thread;
    private String name = this.toString();

    protected volatile boolean stopped = false;
    protected volatile boolean isShutdown = false;
    private volatile ServiceStatus status = new ServiceStatus(stopped, isShutdown);


    protected AbstractStreamService(String name,
                                    List<ServiceInterface> upstreams,
                                    List<Queue<I>> inputQueues,
                                    List<Queue<O>> outputQueues){
        this.name = name;
        this.upstreams = upstreams;
        this.inputQueues = inputQueues;
        this.outputQueues = outputQueues;

    }

}
