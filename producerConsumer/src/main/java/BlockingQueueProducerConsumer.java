import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueProducerConsumer {
    private static Integer count = 0;
    private static BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<Integer>(10);
    static class Producer implements Runnable {
        @Override
        public void run() {

            while(true){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                try {
                    blockingQueue.put(1);
                    count++;
                    System.out.println(Thread.currentThread().getName()
                            + "生产者生产，目前总共有" + count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        @Override
        public void run() {
            while(true){
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                try {
                    blockingQueue.take();
                    count--;
                    System.out.println(Thread.currentThread().getName()
                            + "消费者消费，目前总共有" + count);
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
