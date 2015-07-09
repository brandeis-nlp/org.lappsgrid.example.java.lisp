package org.lappsgrid.json;

import java.util.Collection;

/**
 * Created by shi on 7/9/15.
 */
public interface Json {

    /** write Json int string **/
    String toString();


    interface Arr extends Json {
        /** read string into Arr **/
         Arr read(String s);

        /** length / get / add / insert / remove **/
         int length();
         Object get(int i);
         Arr add(Object s);
         Arr remove(int i);
         Arr set(int i, Object s);

        /** directly read string Arr as Json Arr object. **/
         Arr convert(String [] arr);
         Arr convert(Collection<String> arr);

        /** clone a Json Arr **/
//         Arr clone();

        /** if has original mapping **/
         Object original();
    }

    interface Obj extends Json {
        /** read string into Obj **/
         Obj read(String s);

        /** has / get / put / remove **/
         boolean has(String key);
         Object get(String key);
         Obj put(String key, Object val);
         Obj remove(String key);

        /**  length / keys **/
         int length();
         Collection<String> keys();

        /** if has original mapping **/
         Object original();

        /** clone a Json Object **/
//         Obj clone();
    }


    interface Proxy {
        /** object into json string */
        String obj2json(Object obj) throws Exception;

        /** string into Object */
        Object str2json(String json) throws Exception;

        Arr newArray();
        Obj newObject();

        Arr convertArray(String [] arr);

        Arr convertArray(Collection<String> arr) ;

        Arr readArray(String s);

        Obj readObject(String s);

        boolean isArray (Object obj) ;
        boolean isObject (Object obj) ;
    }


    abstract class JsonProxy implements Proxy {
        public Object str2json(String json) throws Exception {
            if (json == null)
                return null;
            json = json.trim();
            if (json.startsWith("{")) {
                return readObject(json);
            } else if (json.startsWith("[")) {
                return readArray(json);
            } else {
                return json;
            }
        }


        public Json.Arr newArray() {
            return null;
        }


        public Json.Obj newObject() {
            return null;
        }


        public Json.Arr convertArray(String[] arr) {
            return newArray().convert(arr);
        }


        public Json.Arr convertArray(Collection<String> arr) {
            return newArray().convert(arr);
        }


        public Json.Arr readArray(String s) {
            return newArray().read(s);
        }


        public Json.Obj readObject(String s) {
            return newObject().read(s);
        }


        public boolean isArray(Object obj) {
            if (obj instanceof Json.Arr) {
                return true;
            }
            return false;
        }


        public boolean isObject(Object obj) {
            if (obj instanceof Json.Obj) {
                return true;
            }
            return false;
        }
    }

}
