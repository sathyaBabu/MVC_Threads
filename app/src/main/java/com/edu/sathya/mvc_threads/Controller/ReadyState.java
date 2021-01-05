package com.edu.sathya.mvc_threads.Controller;

/**
 * Created by Sathya on 2/1/2020.
 */

import android.os.Message;
import android.util.Log;

import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.C_DATA;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_DATA;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_QUIT;
import static com.edu.sathya.mvc_threads.Util.ControllerProtocol.V_REQUEST_UPDATE;

// A state pattern shell

final class ReadyState implements ControllerState {

    private final Controller controller;  // has varing state

    public ReadyState(Controller controller) {
        this.controller = controller;
        Log.e("ReadyState", "4. ReadyState Constructor : Caled by Controller..."+this.controller);

        // holds controllers address from the controllers constructor
        // ready state gets created in the constructor of the controller
    }

    @Override // implimented from Controller state
    public final boolean handleMessage(Message msg) {
        switch (msg.what) {
            case V_REQUEST_QUIT:
                onRequestQuit();
                return true;


            // called from View - DemoActivity when button is clicked( update)
            case V_REQUEST_UPDATE:
                onRequestUpdate();
                return true;
            // called from DemoActivity
            // 3. You fall here from (demo Activity) how????
            case V_REQUEST_DATA: // gets called while construction of the controller
                onRequestData();
                return true;
        }
        return false;
    }

    // gets called while construction of the controller.....

    private void onRequestData() {
        // send the data to the outbox handlers (view)
        // we have fetched data from controllers model.getdata()
        // Tell notifyOutBoxHandlers that to take C_DATA and model.data() to outboxhandler
        // call the demoActivity(View) from here
        // c
        controller.notifyOutboxHandlers(C_DATA, 0, 0, controller.getModel().getData());
    }


    // called from View - DemoActivity when button is clicked( update)

    private void onRequestUpdate() {
        // we can't just call model.updateState() here because it will block
        // the inbox thread where this processing is happening.
        // thus we change the state to UpdatingState that will launch and manage
        // a background thread that will do that operation

        // Updatestate obj is created. in that it creates a thread and calls model
        // and update fun in it..
        // the created obj is given to change state
        //  since state of the model is changed
        //  it notifyes out box handlers with a message

        // new UpdatingState(controller) will create a thread and gets to the model to update data
        // at that point of time it calls  controller.notifyOutboxHandlers(C_UPDATE_STARTED, 0, 0, null);
        // this will inform controller update state started with a circular progress bar meaning thread is busy


        // Now the state is in READY_STATE so that user can fetch the data pass that to ChangeState now as follows..

        //  After it omplets the job it 	notifyControllerOfCompletion();

        controller.changeState(new UpdatingState(controller)); // controller has change state in it


        // THread will swing into action .. it updates the model then it calls the following status
        //controller.notifyOutboxHandlers(C_UPDATE_STARTED, 0, 0, null);
        // controller.notifyOutboxHandlers(C_UPDATE_FINISHED, 0, 0, null);


    }

    private void onRequestQuit() {
        controller.quit();
    }
}
