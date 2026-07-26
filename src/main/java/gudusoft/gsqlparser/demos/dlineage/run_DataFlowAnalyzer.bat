echo off

REM # This script/batch file run the gsp demo. 
REM # You need to set the JAVA_HOME environment variable in 
REM # the setenv/setenv.bat file 
REM # before running this script/batch file

REM # Change directory to gsp Demo Home directory
cd ..\..\..\..\..

REM # Run the setenv to set the environment variables.
call setenv\setenv.bat

   
    if NOT EXIST %JAVA_CMD% (
    echo. 
    echo ***************************
    echo JAVA_HOME is not set in the setenv\setenv.bat or not available
    echo Please set the JAVA_HOME. 
    echo eg. JAVA_HOME=C:\Program Files\Java\jdk1.7.0_80
    echo ***************************
    echo.
    cd src\main\java\demos\dlineage
    pause
    goto END
    )

REM # Run the gsp demo
%JAVA_CMD% -cp %CLASSPATH% demos.dlineage.DataFlowAnalyzer  %*

REM # Change back to the original directory
cd src\main\java\demos\dlineage

pause

:END
