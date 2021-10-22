/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.koveloper.thread.utils;

import java.util.LinkedList;

/**
 * Class represents thread unit for handling user events consistently in separate thread
 * @author koveloper
 */
public class TasksThread extends Thread {

    private final LinkedList<Object> queue = new LinkedList<>();
    private final Object mutex = new Object();
    private boolean terminated = false;
    private String str = "";
    private int priority = Thread.NORM_PRIORITY;
    private TasksThreadInterface iface = null;

    /**
     * Create instance with user thread name and default priority Thread.NORM_PRIORITY
     * @param name name of thread
     */
    public TasksThread(String name) {
        str = name;
    }

    /**
     * Create instance with user thread name and user priority
     * @param name name of thread
     * @param priority priority of thread
     */
    public TasksThread(String name, int priority) {
        str = name;
        this.priority = priority;
    }

    /**
     * Create instance with user thread name and user priority
     * @param name name of thread
     * @param priority priority of thread
     * @param iface callback to user interface instance
     */
    public TasksThread(String name, int priority, TasksThreadInterface iface) {
        str = name;
        this.priority = priority;
        this.iface = iface;
    }

    /**
     * Setter for callback to user
     * @param iface callback to user interface instance
     * @return TaskThread this instance
     */
    public TasksThread setIface(TasksThreadInterface iface) {
        this.iface = iface;
        return this;
    }

    @Override
    public void run() {
        setPriority(priority);
        setName(str);
        if(iface != null) {
            iface.started();
        }
        while (!terminated) {
            LinkedList<Object> clone;
            synchronized (mutex) {
                clone = (LinkedList<Object>) queue.clone();
                queue.clear();
            }
            if (clone != null) {
                clone.forEach(o -> {
                    if(iface != null) {
                        iface.handleTask(o);
                    }
                });
            }
            try {
                synchronized (mutex) {
                    if(queue.isEmpty() && !terminated) {
                        mutex.wait();
                    }
                }
            } catch (InterruptedException ex) {
            }
        }
        synchronized(mutex) {
            queue.clear();
        }
        if(iface != null) {
            iface.finished();
        }
    }

    /**
     * Method used for adding tasks to threads queue
     * @param task user object that will be returned via {@link TasksThreadInterface#handleTask} methods
     */
    public void addTask(Object task) {
        if(terminated) {
            return;
        }
        synchronized (mutex) {
            if (!isAlive()) {
                try {
                    start();
                } catch (Exception e) {
                }
            }
            queue.add(task);
            mutex.notify();
        }
    }
    
    /**
     * Method used for kill thread and free any resources
     */
    public void finish() {
        synchronized (mutex) {
            if (!isAlive()) {
                try {
                    start();
                } catch (Exception e) {
                }
            }
            terminated = true;
            mutex.notify();
        }
    }
            
    @Override
    public String toString() {
        return "TasksThread{" + "str=" + str + '}';
    }    
}
