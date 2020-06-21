REM # This script/batch file sets all the envrironment variables required by other batch files and 
REM # scripts inorder to run the demos. 
REM # This script/batch file will be invoked by every other script in the demos directory so that
REM # envrionment variables are properly set before running an application. This ensures that users
REM # have to change the envrironment settings in only one location.


REM # SET PATH FOR Native Libraries
set PATH=%PATH%;lib\;external_lib\

REM # set the Java home directory
rem set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_181
set JAVA_HOME=C:\Program Files\Java\zulu-8-azure-jdk_8.40.0.25-8.0.222-win_x64

set JAVA_CMD="%JAVA_HOME%\bin\java.exe"

set JAVAC_CMD="%JAVA_HOME%\bin\javac.exe"


REM #Set the home directory of the GSP library
set gspDemoHome=.

REM # set classpath to the GSP library Jar files and the database JDBC drivers.
set CLASSPATH=%gspDemoHome%\build;%gspDemoHome%\lib;%gspDemoHome%\lib\*;%gspDemoHome%\external_lib;%gspDemoHome%\external_lib\simple-xml-2.6.2.jar;%gspDemoHome%\external_lib\expr4j.jar;%gspDemoHome%\external_lib\commons-logging-1.1.3.jar;%gspDemoHome%\external_lib\jarLoader.jar;%gspDemoHome%\external_lib\junrar-0.7.jar;%gspDemoHome%\external_lib\fastjson-1.2.41.jar;.;
