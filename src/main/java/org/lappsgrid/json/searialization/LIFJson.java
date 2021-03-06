package org.lappsgrid.json.searialization;

import org.apache.commons.io.IOUtils;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.json.JacksonJsonProxy;
import org.lappsgrid.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shi on 7/9/15.
 */
public class LIFJson {

    static final Logger logger = LoggerFactory.getLogger(LIFJson.class);
    static final Json.Proxy proxy = new JacksonJsonProxy();

    String context =  "http://vocab.lappsgrid.org/context-1.0.0.jsonld";
    String discriminator = null;
    Json.Obj payload = null;
    Json.Obj error = null;
    Json.Obj metadata = null;
    Json.Obj json = null;
    Json.Obj text = null;
    Json.Arr views = null;


    public String getText() {
        return text.getString("@value");
    }

    public void setText (String text) {
        this.text.put("@value", text);
    }

    public LIFJson() {
        json = proxy.newObject();
        discriminator = Discriminators.Uri.JSON_LD;
        payload = proxy.newObject();
        metadata = proxy.newObject();
        error = proxy.newObject();
        text = proxy.newObject();
        views = proxy.newArray();
    }

    public LIFJson(String textjson) {
        json = proxy.newObject().read(textjson);
        discriminator = json.get("discriminator").toString().trim();
        if (discriminator.equals(Discriminators.Uri.TEXT)) {
            text = proxy.newObject();
            text.put("@value", json.get("payload"));
            // reinitialize other parts.
            discriminator = Discriminators.Uri.JSON_LD;
            payload = proxy.newObject();
            metadata =  proxy.newObject();
            views = proxy.newArray();
        } else if(discriminator.equals(Discriminators.Uri.JSON_LD)) {
            payload = json.getJsonObj("payload");
            text = payload.getJsonObj("text");
            metadata = payload.getJsonObj("metadata");
            if (metadata == null)
                metadata = proxy.newObject();
            views = payload.getJsonArr("views");
            if (views == null)
                views = proxy.newArray();
        }
    }


    public void setDiscriminator(String s) {
        this.discriminator = s;
    }
    public String getDiscriminator() {
        return discriminator;
    }
    public Json.Obj getJsonObj() {
        return json;
    }



    public Json.Obj newViewsMetadata(Json.Obj view){
        Json.Obj metadata = view.getJsonObj("metadata");
        if (metadata == null) {
            metadata = proxy.newObject();
            view.put("metadata", metadata);
        }
        return metadata;
    }


    public Json.Obj newViewswMetadata(Json.Obj view, String key, Object val){
        Json.Obj meta = this.newViewsMetadata(view);
        meta.put(key, val);
        return meta;
    }

    public Json.Obj newContains(Json.Obj view,String containName, String type, String producer){
        Json.Obj meta = this.newViewsMetadata(view);
        Json.Obj contains = meta.getJsonObj("contains");
        if (contains == null) {
            contains = proxy.newObject();
            meta.put("contains", contains);
        }
        Json.Obj contain = proxy.newObject();
        contain.put("producer", producer);
        contain.put("type",type);
        contains.put(containName,contain);
        return contains;
    }

    public Json.Obj newAnnotation(Json.Obj view){
        Json.Obj annotation = proxy.newObject();
        Json.Arr annotations = view.getJsonArr("annotations");
        if (annotations == null) {
            annotations = proxy.newArray();
            view.put("annotations", annotations);
        }
        annotations.add(annotation);
        return annotation;
    }

    public Json.Obj newAnnotation(Json.Obj view, Json.Obj copyfrom) {
        Json.Obj annotation = proxy.newObject().read(copyfrom.toString());
        Json.Arr annotations = view.getJsonArr("annotations");
        if (annotations == null) {
            annotations = proxy.newArray();
            view.put("annotations", annotations);
        }
        annotations.add(annotation);
        return annotation;
    }

    public Json.Obj newAnnotation(Json.Obj view, String label, String id) {
        Json.Obj ann = this.newAnnotation(view);
        ann.put("label", label);
        ann.put("id", id);
        return ann;
    }

    public Json.Obj newAnnotation(Json.Obj view, String label, String id, int start, int end) {
        Json.Obj ann = this.newAnnotation(view);
        ann.put("label", label);
        ann.put("id", id);
        ann.put("start", start);
        ann.put("end", end);
        return ann;
    }


    public Json.Obj newView() {
        Json.Obj view = proxy.newObject();
        Json.Arr annotations = proxy.newArray();
        view.put("metadata", proxy.newObject());
        view.put("annotations", annotations);
        views.add(view);
        return view;
    }

    public void setStart(Json.Obj annotation, int start) {
        annotation.put("start", start);
    }

    public void setEnd(Json.Obj annotation, int end) {
        annotation.put("end", end);
    }

    public void setWord(Json.Obj annotation, String word) {
        setFeature(annotation, "word", word);
    }

    public void setCategory(Json.Obj annotation, String word) {
        setFeature(annotation, "category", word);
    }

    public List<Json.Obj> getLastViewAnnotations() {
        ArrayList<Json.Obj> lastAnnotations = null;
        if(views.length() > 0) {
            for(int i = views.length() - 1; i >= 0; i--) {
                Json.Obj lastView =  (Json.Obj)views.get(i);
                Json.Obj lastViewMeta = (Json.Obj) lastView.get("metadata");
                Json.Arr lastViewAnnotations = (Json.Arr)lastView.get("annotations");
                Json.Obj lastViewContains = (Json.Obj)lastViewMeta.get("contains");
                if (lastViewContains.has(Discriminators.Uri.TOKEN)) {
                    // contains sentence
                    lastAnnotations = new ArrayList<Json.Obj>(lastViewAnnotations.length());
                    for(int j = 0; j < lastViewAnnotations.length(); j++) {
                        Json.Obj lastStepAnnotation = lastViewAnnotations.getJsonObj(j);
                        lastAnnotations.add(lastStepAnnotation);
                    }
                    break;
                }
            }
        }
        return lastAnnotations;
    }


    public int getStart(Json.Obj annotation) {
        return annotation.getInt("start");
    }

    public int getEnd(Json.Obj annotation) {
        return annotation.getInt("end");
    }

    public String getAnnotationText(Json.Obj annotation) {
        int start = getStart(annotation);
        int end = getEnd(annotation);
        return getText().substring(start, end);
    }


    public void setSentence(Json.Obj annotation, String sent) {
        setFeature(annotation, "sentence", sent);
    }


    public void setLabel(Json.Obj annotation, String label) {
        annotation.put("label", label);
    }

    public void setId(Json.Obj annotation, String id) {
        annotation.put("id", id);
    }

    public void setPOSTag(Json.Obj annotation, String posTag) {
        setFeature(annotation, "pos", posTag);
    }

    public void setNamedEntity(Json.Obj annotation, String ne) {
        setFeature(annotation, "ne", ne);
    }

    public void setError(String msg, String stacktrace) {
        this.setDiscriminator(Discriminators.Uri.ERROR);
        Json.Obj val = proxy.newObject();
        val.put("@value", msg);
        val.put("stacktrace", stacktrace);
        error.put("text",  val);
    }


    public void setFeature(Json.Obj annotation, String name,  Object value) {
        Json.Obj features = annotation.getJsonObj("features");
        if (features == null) {
            features = newFeatures(annotation);
        }
        features.put(name, value);
    }

    public Json.Obj newFeatures(Json.Obj annotation) {
        Json.Obj features = proxy.newObject();
        annotation.put("features", features);
        return features;
    }

    public String toString(){
        json.put("discriminator" ,discriminator);
        if (discriminator.equals(Discriminators.Uri.JSON_LD)) {
            json.put("payload" ,payload);
            payload.put("@context",context);
            payload.put("metadata", metadata);
            payload.put("text", text);
            payload.put("views", views);
        } else if(discriminator.equals(Discriminators.Uri.ERROR)) {
            json.put("payload" ,error);
        }
        return json.toString();
    }


    /**
     * searialize throwable object into LIF json.
     * @param th
     * @return
     */
    public static String toString(Throwable th) {
        Json.Obj json = proxy.newObject();
        json.put("discriminator", Discriminators.Uri.ERROR);
        Json.Obj error = proxy.newObject();
        error.put("message", th.getMessage());
        StringWriter sw = new StringWriter();
        th.printStackTrace( new PrintWriter(sw));
        error.put("stacktrace", sw.toString());
        json.put("payload", error);
        return json.toString();
    }

    /**
     * find meta json file from src/main/resources/metadata directory
     * @param cls
     * @return
     * @throws Exception
     */
    public static String meta(Class cls) throws Exception {
        String meta = IOUtils.toString(cls.getResourceAsStream("/metadata/"+ cls.getName() +".json"));
        Json.Obj json = proxy.newObject();
        json.put("discriminator", Discriminators.Uri.META);
        json.put("payload", proxy.newObject().read(meta));
        logger.info("---------------------META:-------------------");
        logger.info(json.toString());
        return json.toString();
    }

    public static boolean isJsonObj(String s) {
        if (s != null) {
            s = s.trim();
            if (s.startsWith("{") && s.endsWith("}")){
                //TODO: more strict validate
                return true;
            }
        }
        return false;
    }


    public static File getResourceFile(String name) {
        try {
            return new File(LIFJson.class.getResource("/" + name).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String getResourcePath(String name) {
        try {
            return new File(LIFJson.class.getResource("/" + name).toURI()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }


}
