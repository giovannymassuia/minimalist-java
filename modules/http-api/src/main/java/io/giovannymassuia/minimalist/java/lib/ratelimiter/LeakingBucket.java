package io.giovannymassuia.minimalist.java.lib.ratelimiter;

import io.giovannymassuia.minimalist.java.lib.Route.RoutePath;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

class LeakingBucket implements RateLimiter {

    private final Logger logger = Logger.getLogger(LeakingBucket.class.getName());

    private static final int DEFAULT_BUCKET_SIZE = 5;
    private static final Duration DEFAULT_REFILL_RATE = Duration.ofSeconds(1);

    private final int bucketSize;
    private final long leakRateInMillis;

    private final BlockingQueue<Runnable> bucketQueue;
    private final AtomicInteger currentQueueSize;

    private final ScheduledExecutorService scheduler;

    LeakingBucket() {
        this(DEFAULT_BUCKET_SIZE, DEFAULT_REFILL_RATE);
    }

    LeakingBucket(int bucketSize, Duration leakRate) {
        if (bucketSize <= 0) {
            throw new IllegalArgumentException("Bucket size can not be less or equal to zero.");
        }
        if (leakRate == null) {
            throw new IllegalArgumentException("Refill rate should not be null.");
        }
        this.bucketSize = bucketSize;
        this.leakRateInMillis = leakRate.getSeconds() * 1000;

        this.bucketQueue = new LinkedBlockingQueue<>(bucketSize);
        this.currentQueueSize = new AtomicInteger(0);

        this.scheduler = Executors.newScheduledThreadPool(1);
        startLeaking();
    }

    @Override
    public boolean checkAndProcess(RoutePath routePath, Runnable runnable) {
        if (currentQueueSize.get() <= bucketSize
            && currentQueueSize.incrementAndGet() <= bucketSize) {
            return bucketQueue.offer(runnable);
        } else {
            currentQueueSize.decrementAndGet(); // Revert increment if queue is full
            return false; // Bucket is full, task rejected
        }
    }

    private void startLeaking() {
        scheduler.scheduleAtFixedRate(() -> {
            Runnable task = bucketQueue.poll();
            if (task != null) {
                currentQueueSize.decrementAndGet();
                // Execute the task
                task.run();
            }
        }, 0, leakRateInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void shutdownGracefully() {
        System.out.println("Shutting down " + this.getClass().getSimpleName() + "...");

        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }

        System.out.println(this.getClass().getSimpleName() + " terminated scheduler.");
    }

    public int getQueueSize() {
        return currentQueueSize.get();
    }
}