@ECHO OFF
SETLOCAL enableDelayedExpansion

SET cur_dir=%CD%
echo %cur_dir%

SET qddemo=%cur_dir%

IF EXIST %qddemo%\lib	RMDIR %qddemo%\lib
IF NOT EXIST %qddemo%\lib  MKDIR %qddemo%\lib
MKDIR %qddemo%\lib
XCOPY  ..\..\..\..\..\lib\*  %qddemo%\lib
XCOPY  ..\..\..\..\..\external_lib\sqlflow-exporter.jar  %qddemo%\lib

SET qddemo_bin=%qddemo%\lib
SET qddemo_class=%qddemo%\class

echo %qddemo_class%
echo %qddemo_bin%

IF EXIST %qddemo_class%	RMDIR %qddemo_class%
IF NOT EXIST %qddemo_class%  MKDIR %qddemo_class%

cd %cur_dir%
FOR /R %%b IN ( . ) DO (
IF EXIST %%b/*.java  SET JFILES=!JFILES! %%b/*.java
)

MKDIR %qddemo_class%\lib
XCOPY  %qddemo_bin%  %qddemo_class%\lib
XCOPY  %qddemo%\MANIFEST.MF  %qddemo_class%

cd %cur_dir%

    javac -d %qddemo_class% -encoding utf-8 -cp .;%qddemo_bin%\*; %JFILES%

cd %qddemo_class%
    jar -cvfm %qddemo%\data_flow_analyzer.jar %qddemo%\MANIFEST.MF *

echo "successfully"

pause
