package com.suhel.gameoflife;

import android.os.Handler;
import android.os.Looper;

public abstract class SimpleTimer extends Thread {

    private volatile boolean isRunning = true;
    private volatile boolean isPaused = false;
    private volatile long delay = 1000;
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    @Override
    public void run() {
        while (isRunning) {
            try {
                while (isPaused) {
                }
                Thread.sleep(delay);
                mainThread.post(this::tick);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void setDelay(long delay) {
        this.delay = delay;
    }

    public synchronized long getDelay() {
        return delay;
    }

    public synchronized void setPaused(boolean paused) {
        isPaused = paused;
    }

    public synchronized boolean isPaused() {
        return isPaused;
    }

    public synchronized void setRunning(boolean running) {
        isRunning = running;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    public abstract void tick();

}
