package com.edu.sathya.mvc_threads;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.edu.sathya.mvc_threads.Controller.Controller;
import com.edu.sathya.mvc_threads.model.Model;
import com.edu.sathya.mvc_threads.model.ModelData;

import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_DATA;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_QUIT;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_UPDATE_FINISHED;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_UPDATE_STARTED;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_DATA;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_QUIT;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_UPDATE;

/**
 * Created by Sathya on 2/1/2020.
 */


/*
 *  This way, the View can only turn user events into messages and send them to Controller
        without thinking of how to react to them, and whenever the Controller gives any message
        to the View, the View will mindlessly execute whatever the Controller tells it while
        presenting the application status to the user. Thus we get a clean implementation of the
        MVC paradigm.

 *
 *
 *
 */

//implements Handler.Callback : overides..    @Override public boolean handleMessage(Message msg) {
// onClickListener overrides OnClick()


public class ViewMainActivity extends AppCompatActivity implements Handler.Callback, OnClickListener {

    private static final String TAG = ViewMainActivity.class.getSimpleName();

    private Controller controller; // WATCH Controller handle initiates the object.. House Keeping

    // Reffer to Controller for a HEAVY inithouse keeping work

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_main);

        ((Button) findViewById(R.id.update)).setOnClickListener(this);
        ((Button) findViewById(R.id.quit)).setOnClickListener(this);


        Log.e(TAG, "1. Past Update and quit buton now....");
//      Before starting jump to line 71 to check on Controller constructor etting called
        // setting up in and outbox handlers etc


        // jump to the following line on click of a button
        // ----------------------- 197 ------------------------

        // When a button is pushed u will hit point 3 and 4 in onClick() fun

        // Lets do some house keeping job now

        // Pass the model address to controller
        // asn the current thread ID of (From DemoActivity) to controller
        // There u reffer to point 1 and 2



        // u fall here due to default thread..
        // Model obj has a listener ArrayList in it, Then it creates the obj modeldata
        // Sets data and gets data ( an int no )
        // we are passing the model() obj to constructor


        // model object has a framework to store many models but not used in this code...
        controller = new Controller(new Model());

        // Controller prepares an handler thread
        // It instintates ReadyState obj
        // It overrides HandleMessage() and waits for the message


        // We are passing the view( demoActivity thread ) to get registered with controller
        // addOutboxHandler will register new handler threads as many as u create

        // remember following line can be called many times
        // if you think as a login
        // so every line gets registered with outbox handler arraylist

        // when we have 4 diffrent obj give handler1, handler2,handler3, 4

        controller.addOutboxHandler(new Handler(this));

        // create a class chirag/neil/swami/lakshmi/sathya  with a handler in it
        // create all class under view...ViewMainActivity under that file

       // controller.addOutboxHandler(chirag.getHandler()); //new Handler(this));

        // messages will go to .handleMessage()
        // reg the created obj and thread


        // handle of the thread we have just started in controller is returned
        // by getInboxHandler
        // Send a message to that thread via handler  V_REQUEST_DATA in ReadyState obj

        // getInboxHandler() returns the worker thread of controller  we are posting the following message to it
        //  inBoxHandler() is a handler thread which handles looper in it
        //  That in turn will call the readyState Message handling session
        // Controller.this.handleMessage(msg); in readyState

        controller.getInboxHandler().sendEmptyMessage(V_REQUEST_DATA); // request initial data
        // 3.

        // so that ReadyState Design can change the state property
        // By changing the state it calls the relevent fun RequestData()
        // That in turn calls controller.notifyOutboxHandlers(C_DATA, 0, 0, controller.getModel().getData());
        // which calls Thread pool : it will recurse array of handlers
        // Gets the data from Modeldata(), Checks the message pool for c_data
        // which happens to be a part of view : DemoAitivity from there it calls
        // OnData(msg.obj) msg.obj has the data which model has returned
        // That gets displayed.......
        //


        // After that it has to update model too...


        /*
    	         inbox :
    	 *
    	 *
    	 * Give the Controller an inbox handler � so that the View layer can send
    	 *  messages to it, such as �the user asked us to quit� or
    	 *   �the user asked us to update the data�.
    	 *
    	 *
    	 *
    	 *
    	 *    outbox
    	 *
    	 *
    	 *     * Give the ability to attach one or multiple outbox handlers to the Controller, so that the
	 *  Controller can send its own messages to the View (or Views) � such as �model update finished�
	 *   or �destroy yourself, we are quitting� etc.
	 *
        This way, the View can only turn user events into messages and send them to Controller
        without thinking of how to react to them, and whenever the Controller gives any message
        to the View, the View will mindlessly execute whatever the Controller tells it while
        presenting the application status to the user. Thus we get a clean implementation of the
        MVC paradigm.

    	 */

    }

    @Override
    protected void onDestroy() {
        // I think it is a good idea to not fail in onDestroy()
        try {
            controller.dispose();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to destroy the controller", t);
        }

        super.onDestroy();
    }

    // This is view message handler  can interpret the following messages
    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "Received message: " + msg);

        switch (msg.what) {
            case C_QUIT:
                onQuit();
                return true;
            case C_DATA:
                // called from ReadyState
                //controller.notifyOutboxHandlers(C_DATA, 0, 0, controller.getModel().getData());

                onData((ModelData) msg.obj);
                // Sets the data in the view text box

                return true;
            case C_UPDATE_STARTED:
                onUpdateStarted();
                return true;
            case C_UPDATE_FINISHED:
                onUpdateFinished();
                return true;
        }
        return false;
    }
    // panda boy 080 4094 2273
    private void onUpdateStarted() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void onUpdateFinished() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        // request the updated data from Ready_State
        // That in turn gets the data from model
        // Passes it to notify via c_data to Demo_Activity to display
        controller.getInboxHandler().sendEmptyMessage(V_REQUEST_DATA);
    }

    private void onData(ModelData data) {
        TextView dataView = (TextView) findViewById(R.id.data_view);
        dataView.setText("Data Fetched... :  "+ data.getAnswer());
    }

    private void onQuit() {
        Log.d(TAG, "Activity quitting");
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                // 3. getInboxHandler() returns current thread. post a message to it
                // Handler inboxHandler;
                // calls this in ready state
                controller.getInboxHandler().sendEmptyMessage(V_REQUEST_UPDATE);/// NOTE its UPDATE
                // Controller objects handle ( reffer to that now in line number : 83 & 104
                // fetches the handle of controller to that we are sending a message  V_REQUEST_UPDATE
                // Controllers handle in turn calls the Ready State obh.handleMessage()
                // so state handles our message now..
                //
                // Controller.this.handleMessage(msg);
                // This(controller) has a handle to model,state
                // when u call this.handleMessage ReadyState obj.handleMessage() is fired
                // line 23 in ReadyState..

                // line 31 in ReadyState calls line 45 to  onRequestData()
                //
                // send the data to the outbox handlers (view)
                // we have fetched data from controllers model.getdata()
                // Tell notifyOutBoxHandlers that to take C_DATA and model.data() to outboxhandler
                // call the demoActivity(View) from here
                // c
                // controller.notifyOutboxHandlers(C_DATA, 0, 0, controller.getModel().getData());
                //controller.GetModel is in line 152 in controller
                // that model.getData() in turn calls model object that returns a random data to the fun notifyOutBoxHandlers
                // notify in turn will send the message to the registered DemoActivity's view C_DATA
                // fro there data is set to be diaplayed on view..



                break;
            case R.id.quit:
                //4.
                controller.getInboxHandler().sendEmptyMessage(V_REQUEST_QUIT);
                break;
        }
    }
}