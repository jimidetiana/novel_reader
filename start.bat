@echo off
echo 启动AI小说阅读器...
echo.

REM 检查Java版本
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java，请确保已安装Java 17或更高版本
    pause
    exit /b 1
)

REM 检查Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Maven，请确保已安装Maven 3.6或更高版本
    pause
    exit /b 1
)

echo 正在编译项目...
call mvn clean install -DskipTests

if errorlevel 1 (
    echo 编译失败，请检查错误信息
    pause
    exit /b 1
)

echo.
echo 启动应用...
echo 应用将在 http://localhost:8080 启动
echo 按 Ctrl+C 停止应用
echo.

call mvn spring-boot:run

pause 