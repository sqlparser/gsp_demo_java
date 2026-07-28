REM # This script/batch file sets all the envrironment variables required by other batch files and
REM # scripts inorder to run the demos.
REM # This script/batch file will be invoked by every other script in the demos directory so that
REM # envrionment variables are properly set before running an application. This ensures that users
REM # have to change the envrironment settings in only one location.


REM # SET PATH FOR Native Libraries
set PATH=%PATH%;external_lib\

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

REM # No jars are committed to this repository, so fetch them into external_lib\
REM # the first time. This is a no-op once they are there.
REM #
REM # A vendored parser is what previously let the demos compile against a build
REM # years older than their own source, so every jar is resolved from Maven
REM # instead and none are checked in.
for %%f in (%gspDemoHome%\external_lib\gsqlparser-*.jar) do goto :gspFound
call "%gspDemoHome%\setenv\fetch-parser.bat"
:gspFound

REM # set classpath to the GSP library and the rest of the demo dependencies.
REM #
REM # There used to be a second entry here, %gspDemoHome%\lib\*, for a committed
REM # directory of <scope>system</scope> jars. Those became ordinary Maven
REM # dependencies when pom_dlineage.xml was merged into pom.xml, lib\ was
REM # deleted, and fetch-parser.bat now brings the same jars into external_lib\
REM # via dependency:copy-dependencies. One directory, one source, nothing to
REM # keep in sync by hand.
set CLASSPATH=.;%gspDemoHome%\build;%gspDemoHome%\external_lib\*
