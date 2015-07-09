package org.lappsgrid.example.java.lisp.ws;


import org.apache.commons.io.IOUtils;
import org.lappsgrid.api.WebService;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.example.java.lisp.LispCaller;
import org.lappsgrid.json.JacksonJsonProxy;
import org.lappsgrid.json.Json;
import org.lappsgrid.json.searialization.LIFJson;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

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
                lif.setText(s); // put pure text into lif.
            }
            return execute(lif);
        }catch(Throwable th) {
            th.printStackTrace();
            return LIFJson.toString(th);
        }
    }


    public String execute(LIFJson lif) throws Exception {
        String txt = lif.getText();
        Json.Obj view  = lif.newView();
        lif.newContains(view, "Sparser", "sparser:sift.net", this.getClass().getName());
        Json.Obj ann = lif.newAnnotation(view);
        String ret = parse(txt);
        lif.setStart(ann, 0);
        lif.setEnd(ann, ret.length());
        lif.setSentence(ann, ret);
        lif.setFeature(ann, "caller", "groovy");
//        lif.setFeature(ann, "caller", "bash");
//        lif.setFeature(ann, "caller", "clojure");
//        lif.setFeature(ann, "caller", "abcl");
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
    public String parse(String s) throws Exception{
        /** groovy **/
        Map res = LispCaller.call(LispCaller.LispType.Groovy, null, null, null, s);

        /** bash **/
//        Map res = LispCaller.call(LispCaller.LispType.Bash, null, null, null, s);

        /** clojure **/
//        Map res = LispCaller.call(LispCaller.LispType.Clojure, LIFJson.getResourceFile("echo.clj"), null, "echo", s);
//        return res.get(LispCaller.Result_Name_Output).toString();

        /** abcl **/
//        Map res = LispCaller.call(LispCaller.LispType.ABCL, LIFJson.getResourceFile("echo.lisp"), null, "echo", s);

        return res.get(LispCaller.Result_Name_Output).toString();

    }
}
