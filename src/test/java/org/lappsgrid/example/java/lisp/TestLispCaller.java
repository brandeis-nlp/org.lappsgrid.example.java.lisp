package org.lappsgrid.example.java.lisp;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

public class TestLispCaller{


    public File getResourceFile(String name) {
        try {
            return new File(this.getClass().getResource("/"+name).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    public void testCallClojure () throws LispCallerException {
        Object ret = LispCaller.callClojure(getResourceFile("helloworld.clj"), null, "hello", "world");
        System.out.println(ret);

    }



    @Test
    public void testCallABCL () throws LispCallerException {
//        Object ret = LispCaller.callABCL(getResourceFile("helloworld.lisp"), null, "HELLO");
//        System.out.println(ret);
        Object ret = LispCaller.callABCL(getResourceFile("hello.lisp"), null, "hello", "you");
        System.out.println(ret);

    }


    @Test
    public void testCallBash () throws LispCallerException {
//        Object ret = LispCaller.callBash(null, "hello");
//        System.out.println(ret);
    }

    @Test
    public void testCallGroovy () throws LispCallerException {
        Object ret = LispCaller.callGroovy(null, "hello");
        System.out.println(ret);
    }


    public void testEvalABCL () throws LispCallerException {
//        Object ret = LispCaller.evalABCL(getResourceFile("hello.lisp"));
//        System.out.println(ret);
    }


}