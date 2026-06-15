@echo off
REM Helper script to run Maven with correct JAVA_HOME (JDK 17)
REM Usage: run_maven.bat [maven goals]
REM Example: run_maven.bat spring-boot:run
REM Example: run_maven.bat clean compile
REM Example: run_maven.bat test

set "JAVA_HOME=C:\Progra~1\Java\jdk-17"
set "MVN=D:\#_SWP\#SWP-Group02\su26-swp391-se2023-g2\04-Implement\hoanien-backend\apache-maven-3.9.6\bin\mvn.cmd"

echo [INFO] JAVA_HOME = %JAVA_HOME%
echo [INFO] Running: mvn %*
echo.

"%MVN%" %*
