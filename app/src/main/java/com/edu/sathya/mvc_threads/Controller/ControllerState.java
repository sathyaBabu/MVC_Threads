package com.edu.sathya.mvc_threads.Controller;

import android.os.Message;

/**
 * Created by Sathya on 2/1/2020.
 */

public interface ControllerState {
    boolean handleMessage(Message msg);
}
