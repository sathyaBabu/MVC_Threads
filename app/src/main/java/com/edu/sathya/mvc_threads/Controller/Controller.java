package com.edu.sathya.mvc_threads.Controller;

/**
 * Created by Sathya on 2/1/2020.
 */


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.edu.sathya.mvc_threads.model.Model;

import java.util.ArrayList;
import java.util.List;

import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_QUIT;

public class Controller {

    private static final String TAG = Controller.class.getSimpleName();

    private final Model model;

    private final HandlerThread inboxHandlerThread;

    /*
     * Give the Controller an inbox handler � so that the View layer can send messages to it,
     * such as �the user asked us to quit� or �the user asked us to update the data�.
     *
     *
     */
    private final Handler inboxHandler;


	/*
	 *
	 * Give the ability to attach one or multiple outbox handlers to the Controller, so that the
	 *  Controller can send its own messages to the View (or Views) � such as �model update finished�
	 *   or �destroy yourself, we are quitting� etc.
	 *
        This way, the View can only turn user events into messages and send them to Controller
        without thinking of how to react to them, and whenever the Controller gives any message
        to the View, the View will mindlessly execute whatever the Controller tells it while
        presenting the application status to the user. Thus we get a clean implementation of the
        MVC paradigm.
	 *
	 *
	 *
	 *
	 *
	 */



    private final List<Handler> outboxHandlers = new ArrayList<Handler>();

    private ControllerState state;

    public Controller(Model model) {
        // Controller is bean given model address and
        // current thread ID of (From DemoActivity)
        // reffer to point 1 and 2..
        Log.e(TAG, "2. Controller constructor now....");


        // Stores the model object here....   1 (From DemoActivity)
        this.model = model;

        // Init a handle to a thread that can use internal looper for messaging.. and to handle masages Get and Set in the queue
        // Handler thread is created to store and ret messages via looper in our code below..
        inboxHandlerThread = new HandlerThread("Controller Inbox"); // note you can also set a priority here
        inboxHandlerThread.start();

        Log.e(TAG, "Controller : 3ad Worker thread created.....");

        // Store the action as off now the state is controller
        //this.state means ControllerState state

        // Remember controller is dependent on state machine
        // so lets get it registered...

        this.state = new ReadyState(this);

        Log.e(TAG, "5. Back to Controller constructor now...."+this.state);

        // get the handle to the current thread
        // keep getting message out of the thread looper
        // opening inboxhandler to get view related messages like user wants to getdata,quit etc

        inboxHandler = new Handler(inboxHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                //.....................
                //
                Log.e(TAG, "6. Controller in handler  now...."+inboxHandler);

                //what is this doin here
                Controller.this.handleMessage(msg);
                // This(controller) has a handle to model,state
                // when u call this.handleMessage ReadyState obj.handleMessage() is fired
                // line 23 in ReadyState..

            }
        };
    }



    public final void dispose() {
        // ask the inbox thread to exit gracefully
        inboxHandlerThread.getLooper().quit();
    }

    public final Handler getInboxHandler() {
        return inboxHandler;
    }

    // 2.   is receiving ( DemoActivity) thread handle here.. from (From DemoActivity)
    // Then store the thread handle in list outboxHandlers...
    public final void addOutboxHandler(Handler handler) {
        Log.e(TAG, "2.1 Reg the controllers thread now...."+handler);

        outboxHandlers.add(handler);
    }

    public final void removeOutboxHandler(Handler handler) {
        outboxHandlers.remove(handler);
    }


    // C_DATA and objmodel.data() lands here

    final void notifyOutboxHandlers(int what, int arg1, int arg2, Object obj) {
        if (outboxHandlers.isEmpty()) {
            Log.w(TAG, String.format("No outbox handler to handle outgoing message (%d)", what));
        } else {
            for (Handler handler : outboxHandlers) {

                // get the message from the looper que(thread pool)  with the 4 parameters
                // and dispatch to the relevent obj meaning all handlers
                // which ever handler is active with the said signatures
                // msg.sendToTarget will do the job

                Message msg = Message.obtain(handler, what, arg1, arg2, obj);
                msg.sendToTarget(); //picks  the current thread

                // Here C_DATA and model.getdata() is been given to view
                // passed this data to DemoActivity handler
            }
        }
    }

    private void handleMessage(Message msg) {
        Log.d(TAG, "Received message: " + msg);

       // Call the state handleMessage to vslidate..
        if (! state.handleMessage(msg)) {
            Log.w(TAG, "Unknown message: " + msg);
        }
    }

    final Model getModel() {
        return model;
    }

    final void quit() {
        notifyOutboxHandlers(C_QUIT, 0, 0, null);
    }

    // This is where state change is recorded. This takes a major decision
    // receives 	controller.changeState(new ReadyState(controller));

    final void changeState(ControllerState newState) {
        Log.d(TAG, String.format("Changing state from %s to %s", state, newState));
        state = newState;
    }
}
