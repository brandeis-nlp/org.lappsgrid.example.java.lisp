package org.lappsgrid.example.java.lisp;

import clojure.lang.RT;
import clojure.lang.Var;
import clojure.lang.Compiler;
import org.apache.commons.io.FileUtils;
import org.armedbear.lisp.*;
import org.armedbear.lisp.Package;
import sun.net.www.protocol.file.FileURLConnection;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.armedbear.lisp.Lisp.NIL;

public class LispCaller {

    public enum LispType {
        ABCL,
        Clojure
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

    public static Object call (LispType type, File lispFile,String ns, String method, Object... params) throws LispCallerException{
        if(!lispFile.exists()) {
            throw new LispCallerException("Lisp file does NOT exist: " + lispFile);
        }
        if(type == LispType.ABCL){
            return callABCL(lispFile, ns, method, params);
        }else if(type == LispType.Clojure){
            return callClojure(lispFile, ns, method, params);
        }

        return null;
    }


    protected static Object evalABCL(File abclFile) throws LispCallerException{
        Interpreter interpreter = Interpreter.getInstance();
        if(interpreter == null) {
            interpreter = Interpreter.createInstance();
        }
        String abcl = null;
        try {
            abcl = FileUtils.readFileToString(abclFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new LispCallerException("Read ABCL Script FAILURE: ("+ abclFile +" )!", e);
        }
        LispObject ret = interpreter.eval(abcl);
        return ret;
    }


    protected static Object callABCL(File abclFile, String pckg, String method, Object... params) throws LispCallerException{
        Interpreter interpreter = Interpreter.getInstance();
        if(interpreter == null) {
            interpreter = Interpreter.createInstance();
        }
        String abcl = null;
        try {
            abcl = FileUtils.readFileToString(abclFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new LispCallerException("Read ABCL Script FAILURE: ("+ abclFile +" )!", e);
        }
        LispObject lobj = interpreter.eval(abcl);
        if (pckg == null)
            pckg = "COMMON-LISP-USER";
        org.armedbear.lisp.Package lisppkg = Packages.findPackage(pckg.toUpperCase());
        for (Symbol sym:lisppkg.getAccessibleSymbols()){
            System.out.println(sym.getName());
        }

        org.armedbear.lisp.Symbol symbl = lisppkg.findAccessibleSymbol(method.toUpperCase());
        Function func = (Function) symbl.getSymbolFunction();
        LispObject [] lispobjs = new LispObject[params.length];
        for (int i = 0; i < params.length; i ++) {
            lispobjs[i] = JavaObject.getInstance(params[i]);
        }
        LispObject ret = func.execute(lispobjs);
        return ret;
    }



    /**
     * CLOJURE Requirement
     */
    final static private clojure.lang.Symbol CLOJURE_MAIN = clojure.lang.Symbol.intern("clojure.main");
    final static private Var REQUIRE = RT.var("clojure.core", "require");
    final static private Var LEGACY_REPL = RT.var("clojure.main", "legacy-repl");
    final static private Var LEGACY_SCRIPT = RT.var("clojure.main", "legacy-script");
    final static private Var MAIN = RT.var("clojure.main", "main");

    protected static Object callClojure(File cljFile, String ns, String method, Object... params) throws LispCallerException{
        try {
            Compiler.load(new InputStreamReader(new FileInputStream(cljFile), Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LispCallerException("Load Clojure Script FAILURE: ("+ cljFile.getAbsolutePath()+" )!", e);
        }
        if (ns == null)
            ns = "clojure.core";
        Var v = RT.var(ns, method);
        return v.applyTo(RT.seq(params));
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
