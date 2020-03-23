import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockProducerConsumer {
    private static Integer count = 0;
    private static final Integer FULL = 10;
    private static Lock lock = new ReentrantLock();
    private static Condition notFull = lock.newCondition();
    private static Condition notEmpty = lock.newCondition();

    static class Producer implements Runnable {

        @Override
        public void run() {
            for(int i=0;i<10;i++){
                lock.lock();
                try{
                    while(count==FULL) {
                        try {
                            notFull.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    count++;
                    System.out.println(Thread.currentThread().getName() + "生产者生产，目前总共有" + count);
                    notEmpty.signal();
                } finally {
                    lock.unlock();
                }

            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {

            for(int i=0;i<10;i++){
                lock.lock();
                try{
                    while(count==0) {
                        try {
                            notEmpty.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    count--;
                    System.out.println(Thread.currentThread().getName() + "消费者消费，目前总共有" + count);
                    notFull.signal();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String [] args){
        Thread t1 = new Thread(new Producer());
        t1.start();
        Thread t2 = new Thread(new Consumer());
        t2.start();
        Thread t3 = new Thread(new Consumer());
        t3.start();
    }


}
