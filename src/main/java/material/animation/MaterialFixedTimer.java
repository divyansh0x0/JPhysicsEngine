package material.animation;

import material.utils.Log;

import java.util.ArrayList;

public abstract class MaterialFixedTimer {
    private static final ArrayList<MaterialFixedTimer> allTimers = new ArrayList<>();
    private int timerIndex = 0;
    private final float delayMs;
    private boolean isRunning;
    /**
     * @param delayMs Delay in milliseconds
     */
    private final Thread timerLoop;
    private boolean isDisposed = false;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(MaterialFixedTimer::disposeAll));
    }

    public static void disposeAll() {
        Log.warn("Disposing " + allTimers.size() + " timers");
        for (MaterialFixedTimer timer : allTimers) {
            timer.dispose();
        }
    }

    public MaterialFixedTimer(float delayMs) {
        this.delayMs = delayMs;
        timerLoop = new Thread(this::loop);
        timerLoop.start();
        allTimers.add(this);
        timerIndex = allTimers.size();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Log.warn(stackTraceElements[2].getFileName() + " created timer " + timerIndex);
    }

    private void loop() {
        long last = 0;
        while (!isDisposed) {
            if (isRunning) {
                if (last == 0)
                    last = System.nanoTime();
                long curr = System.nanoTime();
                long delta = curr - last;

                if (delta > delayMs) {
                    tick(delta * 0.000_001f);
                    last = curr;
                }
                try {
                    synchronized (timerLoop) {
                        timerLoop.wait((short) delayMs);
                    }
                } catch (InterruptedException e) {
                    Log.error("Thread Interrupted");
                }
            } else {
                try {
                    synchronized (timerLoop) {
                        timerLoop.wait((short) 20);
                    }
                } catch (InterruptedException e) {
                    Log.error("Thread Interrupted");
                }

            }
        }
        Log.success("Fixed timer disposed " + timerIndex);

    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            isRunning = false;
        }
    }


    public abstract void tick(float deltaMillis);

    public void dispose() {
        isDisposed = true;
//        timerLoop.interrupt();
    }
}
