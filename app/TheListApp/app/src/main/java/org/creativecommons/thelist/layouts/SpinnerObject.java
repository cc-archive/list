package org.creativecommons.thelist.layouts;

public class SpinnerObject {
    public String name;
    public Object tag;

    public SpinnerObject(String name, String tag){
        this.name = name;
        this.tag = tag;
    }

    public String getName(){
        return name;
    }

    public Object getTag(){
        return tag;
    }

    public String toString(){
        return name;
    }
}
