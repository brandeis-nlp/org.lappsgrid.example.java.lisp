package org.lappsgrid.example.java.lisp;

/**
 * Created by shi on 7/8/15.
 */
public class LispCallerException extends Exception {

    public LispCallerException(){
        super();
    }

    public LispCallerException(String msg){
        super(msg);
    }

    public LispCallerException(String msg, Throwable th){
        super(msg, th);
    }

    public LispCallerException(Throwable th) {
        super(th);
    }
}
