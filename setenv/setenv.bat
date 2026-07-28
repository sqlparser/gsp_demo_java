REM # This script/batch file sets all the envrironment variables required by other batch files and
REM # scripts inorder to run the demos.
REM # This script/batch file will be invoked by every other script in the demos directory so that
REM # envrionment variables are properly set before running an application. This ensures that users
REM # have to change the envrironment settings in only one location.


REM # SET PATH FOR Native Libraries
set PATH=%PATH%;lib\;external_lib\

REM # set the Java home directory.
REM # If JAVA_HOME is already set -- by CI, or by a developer who configured it
REM # once -- keep it. Only fall back to a fixed path when it is not set, so this
REM # file does not have to be edited on a machine that already knows where its
REM # JDK is.
if not defined JAVA_HOME set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_201


set JAVA_CMD="%JAVA_HOME%\bin\java.exe"

set JAVAC_CMD="%JAVA_HOME%\bin\javac.exe"


REM #Set the home directory of the GSP library
set gspDemoHome=.

REM # No parser jar is committed to this repository, so fetch it into
REM # external_lib\ the first time. This is a no-op once it is there.
REM #
REM # A vendored parser is what previously let the demos compile against a build
REM # years older than their own source, so the jar is resolved from Gudu's
REM # public Maven repository instead and never checked in.
for %%f in (%gspDemoHome%\external_lib\gsqlparser-*.jar) do goto :gspFound
call "%gspDemoHome%\setenv\fetch-parser.bat"
:gspFound

REM # set classpath to the GSP library Jar files and the database JDBC drivers.
REM # external_lib comes before lib so the fetched parser always wins over
REM # anything that may be dropped into lib\ later.
set CLASSPATH=.;%gspDemoHome%\build;%gspDemoHome%\external_lib\*;%gspDemoHome%\lib\*
