package org.creativecommons.thelist.misc;

import android.util.Log;

abstract class Duck {


    public void quack() {
        Log.v("DUCK", "QUACK");
    }
    public void swim(){
        //move 10 spaces to the left on Drag
    }

    public void fly(){

    }

    abstract void display(String imageName);
}

