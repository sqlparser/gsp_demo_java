@echo off
REM # Fetches the General SQL Parser jar into external_lib\.
REM #
REM # No parser jar is committed to this repository. A fresh clone has none, by
REM # design: the parser is a released artifact resolved from Gudu's public Maven
REM # repository, and vendoring a copy is how this repository previously ended up
REM # compiling demos against a build years older than their source.
REM #
REM # Called automatically by setenv\setenv.bat when external_lib\ has no parser,
REM # so the compile_<demo>.bat / run_<demo>.bat scripts work on a fresh clone.
REM # Safe to run directly, and a no-op once the jar is there.
REM #
REM # Requires Maven on PATH, once. After that the .bat workflow needs nothing.

setlocal

REM # Run from the repository root regardless of where this was invoked from.
cd /d "%~dp0.."

for %%f in (external_lib\gsqlparser-*.jar) do (
    echo Parser already present: %%f
    endlocal
    exit /b 0
)

REM # Single source of truth for the version: gsp.core.version in pom.xml.
for /f "delims=" %%v in ('mvn -q help:evaluate -Dexpression^=gsp.core.version -DforceStdout 2^>nul') do set GSPVER=%%v

if "%GSPVER%"=="" (
    echo.
    echo ***************************
    echo Could not read gsp.core.version from pom.xml.
    echo Is Maven on your PATH? The parser has to be fetched once:
    echo    mvn dependency:copy -Dartifact=com.gudusoft:gsqlparser:VERSION -DoutputDirectory=external_lib
    echo ***************************
    echo.
    endlocal
    exit /b 1
)

echo Fetching parser %GSPVER% into external_lib\ ...
call mvn -q dependency:copy -Dartifact=com.gudusoft:gsqlparser:%GSPVER% -DoutputDirectory=external_lib
if errorlevel 1 (
    echo Failed to fetch the parser. See the message above.
    endlocal
    exit /b 1
)

echo Done.
endlocal
exit /b 0
