@echo off
REM # Fetches the General SQL Parser jar, and every other dependency the demos
REM # need, into external_lib\.
REM #
REM # No jar of any kind is committed to this repository. A fresh clone has
REM # none, by design: the parser is a released artifact resolved from Gudu's
REM # public Maven repository, and vendoring a copy is how this repository
REM # previously ended up compiling demos against a build years older than its
REM # own source.
REM #
REM # This used to copy the parser alone, because everything else the demos
REM # needed sat in a committed lib\ directory that setenv.bat put on the
REM # CLASSPATH. Those jars were <scope>system</scope> dependencies, which is
REM # deprecated and which no packaging or classpath plugin can see, so they
REM # were replaced by ordinary Maven coordinates and lib\ was deleted. Maven
REM # is now the single source for all of them, and copy-dependencies brings
REM # the whole set down in one go: the parser, fastjson, simple-xml,
REM # org.boris.expr (out of lib-repo\, an in-project Maven repository), JAXB
REM # and SnakeYAML.
REM #
REM # Called automatically by setenv\setenv.bat when external_lib\ has no
REM # parser, so the compile_<demo>.bat / run_<demo>.bat scripts work on a
REM # fresh clone. Safe to run directly, and a no-op once the jars are there;
REM # delete external_lib\ to force a refetch after changing a dependency.
REM #
REM # Requires Maven on PATH, once. After that the .bat workflow needs nothing.

setlocal

REM # Run from the repository root regardless of where this was invoked from.
cd /d "%~dp0.."

REM # The parser is the sentinel for "already fetched": it is the one artifact
REM # guaranteed to be in the set, and the one whose absence breaks everything.
for %%f in (external_lib\gsqlparser-*.jar) do (
    echo Dependencies already present: %%f
    endlocal
    exit /b 0
)

echo Fetching the parser and the other demo dependencies into external_lib\ ...
echo This needs Maven on your PATH, and runs once.

REM # No version is named here on purpose. copy-dependencies reads pom.xml,
REM # which is the single place gsp.core.version is written, so this cannot
REM # drift from what the Maven build resolves. includeScope=runtime keeps the
REM # test-only dependencies out; no .bat script runs tests.
call mvn -q dependency:copy-dependencies -DoutputDirectory=external_lib -DincludeScope=runtime
if errorlevel 1 (
    echo.
    echo ***************************
    echo Failed to fetch the demo dependencies. See the message above.
    echo Is Maven on your PATH? The equivalent by hand is:
    echo    mvn dependency:copy-dependencies -DoutputDirectory=external_lib -DincludeScope=runtime
    echo ***************************
    echo.
    endlocal
    exit /b 1
)

for %%f in (external_lib\gsqlparser-*.jar) do (
    echo Done.
    endlocal
    exit /b 0
)

echo.
echo ***************************
echo Maven reported success but no parser jar reached external_lib\.
echo Check the gsqlparser dependency in pom.xml.
echo ***************************
echo.
endlocal
exit /b 1
