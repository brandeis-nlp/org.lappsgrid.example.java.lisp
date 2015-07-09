#!/usr/bin/env groovy

println "groovy>>" + args[0]

// groovy call "caller.sh" under the same directory
/*

def dir = new File(getClass().protectionDomain.codeSource.location.path).parent
def sh = new File(dir, "caller.sh").getAbsolutePath();

"$sh ${args[0]}".execute().text

*/


args[0]