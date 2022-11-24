# gsp_demo_java
Java demos for the General SQL Parser library

## compile and run
1. open setenv/setenv.bat, and set JAVA_HOME to the path where JDK installed.
2. create a build directory.
2. cd src/main/java/demos
3. enter any sub-directory which includes a demo, for example, cd checksyntax
4. execute compile_checksyntax.bat
5. execute run_checksyntax.bat

### tutorial

- SQL modify and rebuild, SQL refactor.
  - [add/modify/remove a new join](src/test/java/scriptWriter/testModifySql.java)
  - [add/modify/remove filter condition](src/test/java/scriptWriter/testModifySql.java)
  - [add/modify/remove columns in select lis](src/test/java/scriptWriter/testModifySql.java)
  
  
## master and dev branch  
the master branch is updated only a new version of GSP is released on official webstie: 
https://sqlparser.com/download.php

while the dev branch is updated more frequently and may not compile using the gsp.jar 
from the official site or the jar under /lib directory.