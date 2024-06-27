@echo off

setlocal
cd %~dp0\..\..

echo on
..\gradlew --warning-mode all :mqtt-rpc-common:build
