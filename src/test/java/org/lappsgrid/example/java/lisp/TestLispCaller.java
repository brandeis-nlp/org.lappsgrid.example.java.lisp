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

        Object ret = LispCaller.callABCL(getResourceFile("helloworld.abcl"), null, "HELLO");
        System.out.println(ret);
        ret = LispCaller.callABCL(getResourceFile("list.abcl"), null, "HELLOWORLD", "Your");
        System.out.println(ret);

    }


}