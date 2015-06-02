package org.creativecommons.thelist.adapters;

public class ProgressBarState {

    public State mState = State.START;

    public enum State {
        START,
        REQUEST_SENT,
        REQUEST_RECEIVED,
        FINISHED,
        ERROR,
        TIMEOUT
    }

    public void setState(State state){
        mState = state;
    }

}
