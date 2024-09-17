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


# Changes
- [2024/9/17] Changes directory structure and package name,
	all test unit under package name like: package gudusoft.gsqlparser.xxxTest;

Move all test units from 
	c:\prg\gsp_demo_java\src\test\java\
to 
	c:\prg\gsp_demo_java\src\test\java\gudusoft\gsqlparser\
	
	
Move all demos from 
	c:\prg\gsp_demo_java\src\main\java\demos
to 
	c:\prg\gsp_demo_java\src\main\java\gudusoft\gsqlparser\demos	