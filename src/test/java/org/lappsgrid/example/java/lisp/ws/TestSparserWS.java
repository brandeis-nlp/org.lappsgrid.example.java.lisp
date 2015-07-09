package org.lappsgrid.example.java.lisp.ws;

import org.junit.Test;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Container;

import java.util.Map;

/**
 * Created by shi on 7/9/15.
 */
public class TestSparserWS {

    @Test
    public void testExecute(){

        System.out.println("/-----------------------------------\\");
        SparserWS ws = new SparserWS();
        String json = ws.execute("There");
        System.out.println(json);
        Container container = new Container((Map) Serializer.parse(json, Data.class).getPayload());
        System.out.println("\\-----------------------------------/\n");
    }
}
