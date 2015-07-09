package org.lappsgrid.example.java.lisp.ws;


import org.apache.commons.io.IOUtils;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.json.JacksonJsonProxy;
import org.lappsgrid.json.Json;
import org.lappsgrid.json.searialization.LIFJson;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by shi on 7/9/15.
 */
public class SparserWS implements WebService, ISparser {

    @Override
    public String execute(String s) {
        LIFJson lif = null;
        try{
            if (LIFJson.isJsonObj(s)) { //json input
                lif = new LIFJson(s);
                if (lif.getDiscriminator().equals(Discriminators.Uri.ERROR)) {
                    return lif.toString();
                }
            } else { // pure text input
                lif = new LIFJson();
                lif.setError("Only JSON imput is allowed!", "Unkown input: " + s);
                return lif.toString();
            }
            return execute(lif);
        }catch(Throwable th) {
            th.printStackTrace();
            return LIFJson.toString(th);
        }
    }


    public String execute(LIFJson lif) throws Exception {
        
        return lif.toString();
    }


    @Override
    public String getMetadata() {
        try {
            return LIFJson.meta(this.getClass());
        }catch (Throwable th) {
            th.printStackTrace();
            return LIFJson.toString(th);
        }
    }

    @Override
    public String parse(String s) {
        return null;
    }
}
