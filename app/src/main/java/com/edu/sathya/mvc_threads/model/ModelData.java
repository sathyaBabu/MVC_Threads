package com.edu.sathya.mvc_threads.model;

import java.io.Serializable;

/**
 * Created by Sathya on 2/1/2020.
 */
@Immutable
public final class ModelData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int answer;

    public ModelData(int answer) {
        this.answer = answer;
    }

    public final int getAnswer() {
        return answer;
    }
}
