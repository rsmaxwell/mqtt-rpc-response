@echo off

setlocal
cd %~dp0\..\..

echo on
..\gradlew test
