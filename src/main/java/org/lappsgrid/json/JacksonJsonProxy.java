package org.lappsgrid.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;
import java.util.*;

/**
 * Created by shi on 7/9/15.
 */
public class JacksonJsonProxy extends Json.JsonProxy {


    @Override
    public String obj2json(Object obj) throws Exception {
        ObjectWriter ow = new ObjectMapper().writer();
        String json = null;
        try {
            json = ow.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw e;
        }
        return json;
    }

    @Override
    public Json.Arr newArray() {
        return new JacksonJsonArr();
    }

    @Override
    public Json.Obj newObject() {
        return new JacksonJsonObj();
    }

    public static Object valueOf(Object obj) {
        if (obj instanceof  JacksonJsonObj) {
            return ((JacksonJsonObj) obj).original();
        } else if (obj instanceof JacksonJsonArr) {
            return ((JacksonJsonArr) obj).original();
        }
        return obj;
    }


    public static Object value2object(Object obj ) {
        if(obj instanceof List) {
            return new JacksonJsonArr((List)obj);
        } else if(obj instanceof Map) {
            return new JacksonJsonObj((Map)obj);
        }
        return obj;
    }


    public static class JacksonJsonObj implements Json.Obj {

        Map<String, Object> map = null;

        public JacksonJsonObj() {
            map = new LinkedHashMap<String, Object>();
        }

        public JacksonJsonObj(Map<String, Object> map) {
            this.map = map;
        }


        public Json.Obj read(String s) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                map = (Map)mapper.readValue(s, LinkedHashMap.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }


        public boolean has(String key) {
            return map.containsKey(key);
        }


        public Object get(String key) {
            return value2object(map.get(key));
        }


        public Json.Obj put(String key, Object val) {
            map.put(key, valueOf(val));
            return this;
        }


        public Json.Obj remove(String key) {
            map.remove(key);
            return this;
        }


        public int length() {
            return map.size();
        }


        public Collection<String> keys() {
            return map.keySet();
        }


        public Object original() {
            return map;
        }


        public String getString(String key) {
            Object val = this.get(key);
            if (val != null) {
                return (String)val;
            }
            return null;
        }

        public Long getLong(String key) {
            Object val = this.get(key);
            if (val != null) {
                return (Long)val;
            }
            return null;
        }

        public Double getDouble(String key) {
            Object val = this.get(key);
            if (val != null) {
                return (Double)val;
            }
            return null;
        }

        public Boolean getBoolean(String key) {
            Object val = this.get(key);
            if (val != null) {
                return (Boolean)val;
            }
            return null;
        }

        public Integer getInt(String key) {
            Object val = this.get(key);
            if (val != null) {
                return ((Integer)val);
            }
            return null;
        }

        public Arr getJsonArr(String key){
            Object val = this.get(key);
            if (val != null) {
                return (Arr)val;
            }
            return null;
        }
        public Obj getJsonObj(String key){
            Object val = this.get(key);
            if (val != null) {
                return (Obj)val;
            }
            return null;
        }

//
//        public Json.Obj clone() {
//            return new JacksonJson.Obj(new LinkedHashMap<String, Object>(map));
//        }


        public String toString() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this.map);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static class JacksonJsonArr implements Json.Arr {

        List<Object> list = null;

        public JacksonJsonArr (List<Object> list) {
            this.list = list;
        }

        public JacksonJsonArr () {
            list = new ArrayList<Object>();
        }

        public Json.Arr read(String s) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                list = (List<Object>) mapper.readValue(s, List.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }


        public int length() {
            return list.size();
        }


        public Object get(int i) {
            return value2object(list.get(i));
        }


        public Json.Obj getJsonObj(int i) {
            Object val = get(i);
            if (val != null) {
                return (Json.Obj)val;
            }
            return null;
        }

        public Json.Arr getJsonArr(int i) {
            Object val = get(i);
            if (val != null) {
                return (Json.Arr)val;
            }
            return null;
        }

        public String getString(int i) {
            Object val = this.get(i);
            if (val != null) {
                return (String)val;
            }
            return null;
        }

        public Long getLong(int i) {
            Object val = this.get(i);
            if (val != null) {
                return (Long)val;
            }
            return null;
        }

        public Double getDouble(int i) {
            Object val = this.get(i);
            if (val != null) {
                return (Double)val;
            }
            return null;
        }

        public Boolean getBoolean(int i) {
            Object val = this.get(i);
            if (val != null) {
                return (Boolean)val;
            }
            return null;
        }

        public Integer getInt(int i) {
            Object val = this.get(i);
            if (val != null) {
                return (Integer)val;
            }
            return null;
        }

        public Json.Arr add(Object s) {
            list.add(valueOf(s));
            return this;
        }


        public Json.Arr remove(int i) {
            list.remove(i);
            return this;
        }


        public Json.Arr set(int i, Object obj) {
            list.set(i, obj);
            return this;
        }


        public Json.Arr convert(String[] arr) {
            Collections.addAll(list, arr);
            return this;
        }


        public Json.Arr convert(Collection<String> arr) {
            for(String s: arr) {
                list.add(s);
            }
            return this;
        }

//
//        public Json.Arr clone() {
//            return new JacksonJson.Arr(new ArrayList<Object>(list));
//        }


        public Object original() {
            return list;
        }


        public String toString() {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsString(this.list);
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
