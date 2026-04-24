public class Waitandnotify {

    static class SharedResource {
        private int data;
        private boolean bChanged = false;

        public synchronized int get() {
            while (!bChanged) {
                try {
                    System.out.println("Consumer is waiting...");
                    wait();
                } catch (InterruptedException e) {
                    System.out.println("Consumer interrupted.");
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("Consumer got: " + data);
            bChanged = false;
            return data;
        }

        public synchronized void set(int value) {
            data = value;
            bChanged = true;

            System.out.println("Producer set: " + data);

            notify();
        }
    }

    static class Producer implements Runnable {
        private SharedResource resource;

        public Producer(SharedResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                resource.set(i * 10);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    static class Consumer implements Runnable {
        private SharedResource resource;

        public Consumer(SharedResource resource) {
            this.resource = resource;
        }

        @Override
        public void run() {
            for (int i = 1; i <= 5; i++) {
                resource.get();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SharedResource resource = new SharedResource();

        Thread consumer = new Thread(new Consumer(resource));
        Thread producer = new Thread(new Producer(resource));

        consumer.start();
        producer.start();

        consumer.join();
        producer.join();

        System.out.println("Program finished.");
    }
}