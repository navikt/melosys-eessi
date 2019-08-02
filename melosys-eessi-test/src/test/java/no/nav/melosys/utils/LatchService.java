package no.nav.melosys.utils;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class LatchService {
    private CountDownLatch latch;
    private int expected;

    void countDown() {
        if (latch != null) {
            latch.countDown();
        }
    }

    public void reset(int count) {
        latch = new CountDownLatch(count);
        expected = count;
    }

    public void doWait(long time) {
        try {
            if (latch != null) {
                boolean completed = latch.await(time, TimeUnit.MILLISECONDS);
                if (!completed) throw new RuntimeException("WaitTime expired. Expected: " + expected + " Waiting for: " + latch.getCount());
            } else {
                throw new RuntimeException("Latch not initiated");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}