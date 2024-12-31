package com.example.slide.ui;

/**
 * Subject interface for managing Observer registration and notification.
 */
public interface Subject {
    void attach(Observer observer); // Register an observer
    void detach(Observer observer); // Deregister an observer
    void notifyObservers(GuiToken token); // Notify all observers
}
