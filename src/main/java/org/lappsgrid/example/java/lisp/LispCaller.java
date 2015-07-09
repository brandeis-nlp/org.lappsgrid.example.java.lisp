package org.lappsgrid.example.java.lisp;

import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Compiler;
import groovy.lang.*;
import org.apache.commons.io.FileUtils;
import org.armedbear.lisp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

public class LispCaller {

    static final Logger logger = LoggerFactory.getLogger(LispCaller.class);

    public enum LispType {
        ABCL,
        Clojure,
        Bash,
        Groovy
    }

    public static final String Result_Name_Tool = "input.tool";
    public static final String Result_Name_Lisp = "input.lisp";
    public static final String Result_Name_Params = "input.params";
    public static final String Result_Name_NS = "input.namespace";
    public static final String Result_Name_Method = "input.method";
    public static final String Result_Name_Output = "result.output";
    public static final String Result_Name_Error = "result.error";
    public static final String Result_Name_ReturnCode = "result.retcode";
    public static final String Result_Name_Watch = "result.watch";


    private static Map callIO(String tool, File lispFile, Object ... params) throws LispCallerException{
        Map result = new LinkedHashMap();
        List<String> callAndArgs = new ArrayList<String>(params.length + 2);
        callAndArgs.add(tool);
        if(lispFile != null)
            callAndArgs.add(lispFile.getAbsolutePath());
        logger.info("callIO(): lisp=" + lispFile);
        for (Object param: params) {
            callAndArgs.add(param.toString());
        }
        result.put(Result_Name_Tool, tool);
        result.put(Result_Name_Lisp, lispFile);
        result.put(Result_Name_Params, params);
        try{
            ProcessBuilder builder = new ProcessBuilder(callAndArgs);
            Process p = builder.start();
            BufferedReader stdInput =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError =
                    new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String st = null;
            StringBuilder sb = new StringBuilder();
            while ((st = stdInput.readLine()) != null) {
                sb.append(st);
            }
            result.put(Result_Name_Output, sb.toString());
            sb.setLength(0);
            while ((st = stdError.readLine()) != null) {
                sb.append(st);
            }
            result.put(Result_Name_Error, sb.toString());
            int retcode = p.waitFor();
            result.put(Result_Name_ReturnCode, retcode);
            p.destroy();
        }catch(Exception e){
            e.printStackTrace();
            throw new LispCallerException(e);
        }
        return result;
    }




    public static Object eval(LispType type, File lispFile) throws LispCallerException{
        if(!lispFile.exists()) {
            throw new LispCallerException("Lisp file does NOT exist: " + lispFile);
        }
        if(type == LispType.ABCL){
            return evalABCL(lispFile);
        }else if(type == LispType.Clojure){
            return evalClojure(lispFile);
        }
        return null;
    }




    public static Map call (LispType type, File lispFile, String namespace, String method, Object... params) throws LispCallerException{
        if(!lispFile.exists()) {
            throw new LispCallerException("Lisp file does NOT exist: " + lispFile);
        }
        if(type == LispType.ABCL){
            return callABCL(lispFile, namespace, method, params);
        }else if(type == LispType.Clojure){
            return callClojure(lispFile, namespace, method, params);
        }else if(type == LispType.Bash){
            return callBash(lispFile, params);
        }else if (type == LispType.Groovy){
            return callGroovy(lispFile, params);
        }
        return null;
    }





    public static Map callGroovy(File lispFile, Object... params) throws LispCallerException {
        File scriptFile;
        Map result = new LinkedHashMap();
        try {
            scriptFile = new File(LispCaller.class.getResource("/caller.groovy").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new LispCallerException("Tool Script does NOT exist: (caller.groovy)");
        }
        List<String> callAndArgs = new ArrayList<String>();
        logger.info("callGroovy(): lisp=" + lispFile);
        if(lispFile != null) {
            callAndArgs.add(lispFile.getAbsolutePath());
        }
        for (Object param : params) {
            callAndArgs.add(param.toString());
        }
        result.put(Result_Name_Tool, "caller.groovy");
        result.put(Result_Name_Lisp, lispFile);
        result.put(Result_Name_Params, params);

        Binding bnd = new Binding(callAndArgs.toArray(new String[callAndArgs.size()]));
        GroovyShell shell = new GroovyShell(bnd);
        try {

            Object ret = shell.evaluate(scriptFile);
            result.put(Result_Name_Output, ret);
        } catch (IOException e) {
            e.printStackTrace();
            result.put(Result_Name_Error, e);
            throw new LispCallerException("Groovy Evaluate Failure: ( "+scriptFile+" ):", e);
        }
        result.put(Result_Name_Watch, bnd.getVariables());
        return result;
    }


    public static Map callBash(File lispFile, Object... params) throws LispCallerException{
        File scriptFile;
        try {
            scriptFile = new File(LispCaller.class.getResource("/caller.sh").toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new LispCallerException("Tool Script does NOT exist: (caller.sh)");
        }
        Map ret = callIO(scriptFile.getAbsolutePath(), lispFile, params);
        return ret;
    }


    protected static LispObject evalABCL(File abclFile) throws LispCallerException{
        Interpreter interpreter = Interpreter.getInstance();
        if(interpreter == null) {
            interpreter = Interpreter.createInstance();
        }
        String abcl = null;
        try {
            abcl = FileUtils.readFileToString(abclFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new LispCallerException("Read ABCL Script FAILURE: ("+ abclFile +" )!", e);
        }
        LispObject ret = interpreter.eval(abcl);
        return ret;
    }


    protected static Map callABCL(File abclFile, String pckg, String method, Object... params) throws LispCallerException{
        Map result = new LinkedHashMap();
        Interpreter interpreter = Interpreter.getInstance();
        if(interpreter == null) {
            interpreter = Interpreter.createInstance();
        }
        String abcl = null;
        try {
            abcl = FileUtils.readFileToString(abclFile, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            throw new LispCallerException("Read ABCL Script FAILURE: ("+ abclFile +" )!", e);
        }

        LispObject lobj = interpreter.eval(abcl);
        if (pckg == null)
            pckg = "COMMON-LISP-USER";

        result.put(Result_Name_Lisp,abclFile.getAbsoluteFile());
        result.put(Result_Name_NS, pckg);
        result.put(Result_Name_Method, method);
        result.put(Result_Name_Params, params);

        org.armedbear.lisp.Package lisppkg = Packages.findPackage(pckg.toUpperCase());
//        for (Symbol sym:lisppkg.getAccessibleSymbols()){
//            System.out.println(sym.getName());
//        }

        org.armedbear.lisp.Symbol symbl = lisppkg.findAccessibleSymbol(method.toUpperCase());
        Function func = (Function) symbl.getSymbolFunction();
        LispObject [] lispobjs = new LispObject[params.length];
        for (int i = 0; i < params.length; i ++) {
            lispobjs[i] = JavaObject.getInstance(params[i]);
        }
        LispObject ret = func.execute(lispobjs);

        result.put(Result_Name_Output, ret);
        return result;
    }



    /**
     * CLOJURE Requirement
     */
    final static private clojure.lang.Symbol CLOJURE_MAIN = clojure.lang.Symbol.intern("clojure.main");
    final static private Var REQUIRE = RT.var("clojure.core", "require");
    final static private Var LEGACY_REPL = RT.var("clojure.main", "legacy-repl");
    final static private Var LEGACY_SCRIPT = RT.var("clojure.main", "legacy-script");
    final static private Var MAIN = RT.var("clojure.main", "main");

    protected static Map callClojure(File cljFile, String ns, String method, Object... params)
            throws LispCallerException{
        Map result = new LinkedHashMap();
        try {
            Compiler.load(new InputStreamReader(new FileInputStream(cljFile), Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LispCallerException("Load Clojure Script FAILURE: ("+ cljFile.getAbsolutePath()+" )!", e);
        }

        if (ns == null)
            ns = "clojure.core";

        result.put(Result_Name_Lisp,cljFile.getAbsoluteFile());
        result.put(Result_Name_NS, ns);
        result.put(Result_Name_Method, method);
        result.put(Result_Name_Params, params);
        Var v = RT.var(ns, method);
        Object ret = v.applyTo(RT.seq(params));
        result.put(Result_Name_Output, ret);
        return result;
    }


    protected static Object evalClojure(File cljFile) throws LispCallerException{
        try {
            return Compiler.load(new InputStreamReader(new FileInputStream(cljFile), Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LispCallerException("Load Clojure Script FAILURE: ("+ cljFile.getAbsolutePath()+" )!", e);
        }
    }


    public static void main(String[] args) throws Exception {
        // Load the Clojure script -- as a side effect this initializes the runtime.
        String str = "(ns user) (defn foo [a b]   (str a \" \" b))";

//        REQUIRE.invoke(CLOJURE_MAIN);

        Compiler.load(new StringReader(str));

        // Get a reference to the foo function.
        Var foo = RT.var("user", "foo");

        // Call it!
        Object result = foo.invoke("Hi", "there");


//        REQUIRE.invoke(CLOJURE_MAIN);
        MAIN.applyTo(RT.seq(args));

        System.out.println(result);
    }

}
