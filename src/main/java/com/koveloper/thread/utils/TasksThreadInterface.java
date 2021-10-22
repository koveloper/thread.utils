/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.koveloper.thread.utils;

/**
 * Callback interface for {@link TasksThread}
 * @author koveloper
 */
public interface TasksThreadInterface {

    /**
     * Method called from TaskThread on start (after first task added)
     */
    public void started();
    
    /**
     * Method called from TaskThread on handle next user event
     * @param task user object set via {@link TasksThread#addTask} method
     */
    public void handleTask(Object task);
    
    /**
     * Method called on TaskTread unit thread stopped (after finish method called)
     */
    public void finished();
}
