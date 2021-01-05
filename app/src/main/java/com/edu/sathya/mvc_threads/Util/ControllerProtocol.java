package com.edu.sathya.mvc_threads.Util;

/**
 * Created by Sathya on 2/1/2020.
 */
public interface ControllerProtocol {
    int V_REQUEST_QUIT = 101; // empty
    int V_REQUEST_UPDATE = 102; // empty
    int V_REQUEST_DATA = 103; // empty

    int C_QUIT = 201; // empty
    int C_UPDATE_STARTED = 202; // empty
    int C_UPDATE_FINISHED = 203; // empty
    int C_DATA = 204; // obj = (ModelData) data
}
