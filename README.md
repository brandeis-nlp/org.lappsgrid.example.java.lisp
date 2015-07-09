# LispCaller
Lapps Service Framework for Wrapping Lisp software. 


### How to Download and Compile a Web Service?
1. Download: 
	* git clone https://github.com/brandeis-nlp/org.lappsgrid.example.java.lisp.git
2. Compile:
 	* mvn clean package jetty:run
 	* Then do not close this command.
3. Open WSDL:
    * Open http://localhost:4040/sparser/services
    * Test WSDL in soupui: http://localhost:4040/sparser/services/SparserWS?wsdl
4. Test it Online:
    * Open http://localhost:4040/sparser/jsServices
5. Deploy:
    * Run maven package: mvn clean package
    * copy the target/sparser-${version}.war into $TOMCAT_HOME/webapps
6. Remote Access:
    * Similar as  http://server-ip:8080/sparser/services/SparserWS?wsdl 
    * Or http://server-ip:8080/sparser/jsServices     
    
### How to Call LISP Software?


1.  We have echo function in echo.lisp and  echo.clj (under src/main/resources/), which will be called by ABCL and Clojure.
2.  Another way to call lisp through new shell script, in this case, we have caller.sh and caller.groovy (under src/main/resources) can be used.


> ABCL seems not well enough, maybe the best way is to invoke the lisp through a bash/groovy script.



#### Through ABCL

```
/** abcl **/

Map res = LispCaller.call(LispCaller.LispType.ABCL, LIFJson.getResourceFile("echo.lisp"), null, "echo", s);

```
#### Through Clojure

```
/** clojure **/

Map res = LispCaller.call(LispCaller.LispType.Clojure, LIFJson.getResourceFile("echo.clj"), null, "echo", s);


```
#### Through Bash

```
/** clojure **/

Map res = LispCaller.call(LispCaller.LispType.Bash, null, null, null, s);


```
#### Through Groovy    

```
/** groovy **/

 Map res = LispCaller.call(LispCaller.LispType.Groovy, null, null, null, s);

```
 
 The return is Map<String, Object>, which will have all the information needed. 
 The output will be :
 
```       
return res.get(LispCaller.Result_Name_Output).toString();

```
Other things in the return map includes:

```  
    public static final String Result_Name_Tool = "input.tool";
    public static final String Result_Name_Lisp = "input.lisp";
    public static final String Result_Name_Params = "input.params";
    public static final String Result_Name_NS = "input.namespace";
    public static final String Result_Name_Method = "input.method";
    public static final String Result_Name_Output = "result.output";
    public static final String Result_Name_Error = "result.error";
    public static final String Result_Name_ReturnCode = "result.retcode";
    public static final String Result_Name_Watch = "result.watch"; 
 
```

