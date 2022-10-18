package com.artemchep.basics_multithreading.cipher;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class TaskManager {
    private final Queue<Runnable> tasksQueue = new LinkedList<>();
    private volatile boolean isRunning = true;

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    Runnable task = null;
                    try {
                        task = getNewTask();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    assert task != null;
                    task.run();
                }
            }
        });

        thread.start();
    }

    private Runnable getNewTask() throws InterruptedException {
        synchronized (this) {
            while (tasksQueue.isEmpty() && isRunning) {
                wait();
            }

            return tasksQueue.poll();
        }
    }

    public void addTask(Runnable task) {
        synchronized (this) {
            boolean res = tasksQueue.offer(task);
            Log.d("Post Task", String.valueOf(res));
            notify();
        }
    }

    public void stop() {
        synchronized (this) {
            isRunning = false;
            notify();
        }
    }
}
