package com.example.slide.ui;

/**
 * Observer interface for receiving updates about tokens.
 */
public interface Observer {
    void update(GuiToken token); // Ensure the method takes a GuiToken as a parameter
}
