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

REM # set classpath to the GSP library Jar files and the database JDBC drivers.
REM #
REM # external_lib comes BEFORE lib, and that order matters. lib\ still holds old
REM # parser jars (gsqlparser-3.1.1.0, gudusoft.gsqlparser-3.0.2.5) that other
REM # things depend on, and with lib\ first those shadow the current parser: the
REM # demos then fail to compile on symbols the old jars predate, for example
REM # EOBTenantMode in checksyntax. Put the parser you actually want to build
REM # against in external_lib\ and it wins.
REM #
REM # To fetch the current parser into external_lib\, from the repository root:
REM #
REM #   mvn dependency:copy -Dartifact=com.gudusoft:gsqlparser:4.1.6 -DoutputDirectory=external_lib
REM #
set CLASSPATH=.;%gspDemoHome%\build;%gspDemoHome%\external_lib\*;%gspDemoHome%\lib\*
