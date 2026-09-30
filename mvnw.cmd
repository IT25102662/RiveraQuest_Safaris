@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@if "%DEBUG%"=="" @echo off
@classworlds.conf.location=%CLASSWORLDS_CONF%

set ERROR_CODE=0

@REM Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if not "%MAVEN_PROJECTBASEDIR%"=="" goto stripBaseDir
goto setBaseDir

:stripBaseDir
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%
goto stripBaseDir

:setBaseDir
set MAVEN_BATCH_ECHO=off
set MAVEN_BATCH_PAUSE=off

@REM Execute Maven Wrapper or Java Spring Boot
if exist "%~dp0.mvn\wrapper\maven-wrapper.jar" (
    "%JAVA_HOME%\bin\java.exe" -jar "%~dp0.mvn\wrapper\maven-wrapper.jar" %*
) else (
    echo [INFO] Running Spring Boot via Java...
    java -jar target/boat-safari-management-1.0.0.jar
)
