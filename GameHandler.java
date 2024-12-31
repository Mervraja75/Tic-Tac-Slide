package com.example.slide.ui;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import com.example.slide.logic.TickListener;

/**
 * This class pumps out "timer" events at
 * regular intervals, so we can do animation.
 */
public class GameHandler extends Handler {

    private List<TickListener> fans;
    private boolean paused;

    public GameHandler() {
        paused = false;
        fans = new ArrayList<>();
        sendMessageDelayed(obtainMessage(), 100);
    }

    public void register(TickListener t) {
        fans.add(t);
    }

    public void unregister(TickListener t) {
        fans.remove(t);
    }

    public void pause() {
        paused = true;
    }

    public void restart() {
        paused = false;
    }

    @Override
    public void handleMessage(Message m) {
        if (!paused) {
            List<TickListener> snapshot = new ArrayList<>(fans); // Create a snapshot of the list
            for (TickListener b : snapshot) {
                b.onTick();
            }
        }
        sendMessageDelayed(obtainMessage(), 100);
    }


}