package com.artemchep.basics_multithreading.cipher;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

public class TaskManager<T extends Runnable> {
    private final Queue<T> tasksQueue = new LinkedList<>();
    private volatile boolean isRunning = true;

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    T task = getNewTask();
                    task.run();
                }
            }
        });

        thread.start();
    }

    private T getNewTask() {
        synchronized (this) {
            while (tasksQueue.isEmpty() && isRunning) {
                waitForTask();
            }

            return tasksQueue.poll();
        }
    }

    public void addTask(T task) {
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

    private void waitForTask() {
        try {
            Log.d("Queue", "Queue is empty");
            wait();
        } catch (Exception e) {
            Log.d("Exception", String.valueOf(e));
        }
    }
}
