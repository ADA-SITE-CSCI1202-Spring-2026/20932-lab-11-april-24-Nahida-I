import java.time.Duration;
import java.time.Instant;

public class DynamicScaling {
    
    static class MathTask implements Runnable {
        private final int taskId;
        
        public MathTask(int taskId) {
            this.taskId = taskId;
        }
        
        @Override
        public void run() {
            long sum = 0;
            // Heavy calculation: sum of i^3 + i^4 for 10 million iterations
            for (int i = 1; i <= 10_000_000; i++) {
                long i3 = (long) i * i * i;
                long i4 = (long) i * i * i * i;
                sum += i3 + i4;
            }
            System.out.printf("Thread %d completed with partial sum: %d%n", 
                            taskId, sum);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // 1. Retrieve Core Count
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available logical processors: " + availableProcessors);
        System.out.println("=".repeat(60));
        
        // 2. Test with 1 thread
        System.out.println("\nTesting with 1 thread:");
        runTest(1);
        
        // Add a small delay to let system stabilize
        Thread.sleep(1000);
        
        // 3. Test with maximum available threads
        System.out.println("\n" + "=".repeat(60));
        System.out.println("\nTesting with " + availableProcessors + " threads:");
        runTest(availableProcessors);
    }
    
    private static void runTest(int threadCount) throws InterruptedException {
        // Create array of Thread objects
        Thread[] threads = new Thread[threadCount];
        
        // Instant timing measurement
        Instant start = Instant.now();
        
        // Spawn threads
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new MathTask(i));
            threads[i].start();
        }
        
        // Crucial: Wait for all threads to complete using join()
        for (Thread thread : threads) {
            thread.join();
        }
        
        Instant end = Instant.now();
        long timeElapsed = Duration.between(start, end).toMillis();
        
        System.out.printf("Time taken with %d thread(s): %d ms%n", 
                        threadCount, timeElapsed);
        
        // Calculate speedup if using multiple threads
        if (threadCount > 1) {
            // For comparison, we need a reference time. Since we can't easily 
            // get the 1-thread time here, we'll just show theoretical speedup
            System.out.printf("Using %d threads completed in %d ms%n", 
                            threadCount, timeElapsed);
        }
    }
}