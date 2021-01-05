package com.edu.sathya.mvc_threads.model;

/**
 * Created by Sathya on 2/1/2020.
 */

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@ThreadSafe
public class Model {
    public interface Listener {
        void onModelStateUpdated(Model model);
    }

    private ModelData data = new ModelData(0);
    // Constructor takes a no and show data returns the same no. This is all Modeldata does


    // Keep an eye on Listeners here. Understand who is registering or using it
    private final List<Listener> listeners = new ArrayList<Listener>();

    public Model() {

    }











    // Get data() returns the hndle of the ModelData obj
    public final ModelData getData() {
        synchronized (this) {
            return data;  // first while init its 0
        }
    }

    public final void updateData() { // takes a while!
        SystemClock.sleep(5000);
        ModelData newData = new ModelData(new Random().nextInt(10) + 1);

        synchronized (this) {
            data = newData;
        }

        synchronized (listeners) {
            for (Listener listener : listeners) {
                listener.onModelStateUpdated(this);
            }
        }
    }

    public final void addListener(Listener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public final void removeListener(Listener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }
}
