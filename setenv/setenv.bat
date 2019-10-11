REM # This script/batch file sets all the envrironment variables required by other batch files and 
REM # scripts inorder to run the demos. 
REM # This script/batch file will be invoked by every other script in the demos directory so that
REM # envrionment variables are properly set before running an application. This ensures that users
REM # have to change the envrironment settings in only one location.


REM # SET PATH FOR Native Libraries
set PATH=%PATH%;lib\;external_lib\

REM # set the Java home directory
set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_80

set JAVA_CMD="%JAVA_HOME%\bin\java.exe"

set JAVAC_CMD="%JAVA_HOME%\bin\javac.exe"


REM #Set the home directory of the SwisSQL - Console  distribution
set gspDemoHome=.

REM # set classpath to the SwisSQL Jar files and the database JDBC drivers.
set CLASSPATH=%gspDemoHome%\build;%gspDemoHome%\lib;%gspDemoHome%\lib\gudusoft.gsqlparser-2.0.2.3.jar;%gspDemoHome%\external_lib;%gspDemoHome%\external_lib\simple-xml-2.6.2.jar;%gspDemoHome%\external_lib\expr4j.jar;%gspDemoHome%\external_lib\commons-logging-1.1.3.jar;%gspDemoHome%\external_lib\json.jar;%gspDemoHome%\external_lib\jarLoader.jar;%gspDemoHome%\external_lib\junrar-0.7.jar;.;
