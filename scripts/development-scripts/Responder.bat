@echo off
setLocal EnableDelayedExpansion

set BASEDIR=%~dp0

pushd %BASEDIR%
set DEV_SCRIPT_DIR=%CD%
popd

pushd %DEV_SCRIPT_DIR%\..
set SCRIPT_DIR=%CD%
popd

pushd %SCRIPT_DIR%\..
set SUBPROJECT_DIR=%CD%
popd

pushd %SUBPROJECT_DIR%\..
set PROJECT_DIR=%CD%
popd

pushd %SUBPROJECT_DIR%\build
set BUILD_DIR=%CD%
popd



cd %PROJECT_DIR%

set CLASSPATH="%SUBPROJECT_DIR%\build\classes\java\test
for /R .\mqtt-rpc-response\build\libs %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)
for /R .\mqtt-rpc-common\build\libs %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)
for /R .\mqtt-rpc-response\runtime %%a in (*.jar) do (
  set CLASSPATH=!CLASSPATH!;%%a
)
set CLASSPATH=!CLASSPATH!;%SUBPROJECT_DIR%\src\main\resources\log4j2.xml
set CLASSPATH=!CLASSPATH!"


java -classpath %CLASSPATH% com.rsmaxwell.mqtt.rpc.response.Responder --username %MQTT_USERNAME% --password %MQTT_PASSWORD%
