package common;

import java.util.concurrent.ArrayBlockingQueue;

public class backPressureQueue<E> extends ArrayBlockingQueue<E> implements Queue<E> {
    public backPressureQueue(int capacity) {
        super(capacity);
    }
}
