public class waitNotifyProducerConsumer {
    private static Object LOCK = new Object();
    private static final Integer FULL = 10;
    private static Integer EMPTY = 0;
    private static Integer count = 0;
    static class Producer implements Runnable{

        public void run() {
            while(true){
                synchronized (LOCK){
                    while(count.equals(FULL)){
                        try{
                            LOCK.wait();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    count++;
                    System.out.println(Thread.currentThread().getName() + "生产者生产，目前总共有" + count);
                    LOCK.notifyAll();
                }
            }
        }
    }

    static class Consumer implements Runnable{
        public void run() {
            while(true){
                synchronized (LOCK){
                    while(count == 0){
                        try{
                            LOCK.wait();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    count--;
                    System.out.println(Thread.currentThread().getName() + "消费者消费，目前总共有" + count);
                    LOCK.notifyAll();
                }
            }
        }
    }
    public static void main(String []args){

        Thread t1 = new Thread(new Producer());
        Thread t2 = new Thread(new Consumer());
        Thread t3 = new Thread(new Consumer());
        t1.start();
        t2.start();
        t3.start();

    }

}
