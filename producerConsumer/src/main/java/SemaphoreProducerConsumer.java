import java.util.concurrent.Semaphore;

public class SemaphoreProducerConsumer {
    private static Integer count = 0;
    private static Semaphore mutex = new Semaphore(1);
    private static Semaphore notFull = new Semaphore(10);
    private static Semaphore notEmpty = new Semaphore(0);

    static class Producer implements Runnable {
        @Override
        public void run() {
            while(true){
                try {
                    notFull.acquire();
                    mutex.acquire();
                    count++;
                    System.out.println(Thread.currentThread().getName()
                            + "生产者生产，目前总共有" + count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();
                    notEmpty.release();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while(true){
                try {
                    notEmpty.acquire();
                    mutex.acquire();
                    count--;
                    System.out.println(Thread.currentThread().getName()
                            + "消费者生产，目前总共有" + count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    mutex.release();
                    notFull.release();
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
