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
                    Runnable task = getNewTask();
                    task.run();
                }
            }
        });

        thread.start();
    }

    private Runnable getNewTask() {
        synchronized (this) {
            while (tasksQueue.isEmpty() && isRunning) {
                waitForTask();
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

    private void waitForTask() {
        try {
            Log.d("Queue", "Queue is empty");
            wait();
        } catch (Exception e) {
            Log.d("Exception", String.valueOf(e));
        }
    }
}
