package common;

import java.util.concurrent.TimeUnit;

public interface Queue<E> {
    E poll (long timeout, TimeUnit unit) throws InterruptedException;
    boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException;
}
