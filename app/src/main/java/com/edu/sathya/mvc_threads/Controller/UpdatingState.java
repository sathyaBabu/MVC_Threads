package com.edu.sathya.mvc_threads.Controller;

/**
 * Created by Sathya on 2/1/2020.
 */

import android.os.Message;
import android.util.Log;

import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_UPDATE_FINISHED;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_UPDATE_STARTED;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_QUIT;


final class UpdatingState implements ControllerState {

    private static final String TAG = UpdatingState.class.getSimpleName();

    private final Controller controller;
    private final Thread updateThread;
    // this updates the model and notifies as follows.
    // controller.notifyOutboxHandlers(C_UPDATE_FINISHED, 0, 0, null);

    public UpdatingState(Controller controller) {
        this.controller = controller;

        // Remember, the model is thread-safe in our example so we can modify
        // it from multiple threads
        updateThread = new Thread("Model Update") {
            @Override
            public void run() {
                Controller controller = UpdatingState.this.controller;
                try {
                    // calls the updataFata() in model object

                    controller.getModel().updateData();
                    // Creates a rand no and updates the data
                } catch (Throwable t) {
                    Log.e(TAG, "Error in the update thread", t);
                } finally {
                    // Lets inform that to view via following fun
                    notifyControllerOfCompletion();
                }
            }
        };
        updateThread.start();
        controller.notifyOutboxHandlers(C_UPDATE_STARTED, 0, 0, null);
    }

    private void notifyControllerOfCompletion() {
        // this method will be called from the background thread.
        // avoid Controller synchronization - do this in the inbox thread
        // by using Handler.post()
        // GetInboxHandler gets the pointer to handler thread to process the message......
        controller.getInboxHandler().post(new Runnable() {
            @Override
            public void run() {
                // Inform the controller changestate by passing ReadyState which just got
                // changed, so it sets the state to the new changed state

                controller.changeState(new ReadyState(controller));

                // Then inform DemoActivity - View that update_Finished
                // so it stopes the progress bar
                // Inform the view to Display latest data

                controller.notifyOutboxHandlers(C_UPDATE_FINISHED, 0, 0, null);
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case V_REQUEST_QUIT:
                onRequestQuit();
                return true;
        }
        // ignore all other messages
        return false;
    }

    private void onRequestQuit() {
        updateThread.interrupt();
        controller.quit();
    }
}
