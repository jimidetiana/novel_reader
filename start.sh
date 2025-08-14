#!/bin/bash

echo "启动AI小说阅读器..."
echo

# 检查Java版本
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java，请确保已安装Java 17或更高版本"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven，请确保已安装Maven 3.6或更高版本"
    exit 1
fi

echo "正在编译项目..."
mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "编译失败，请检查错误信息"
    exit 1
fi

echo
echo "启动应用..."
echo "应用将在 http://localhost:8080 启动"
echo "按 Ctrl+C 停止应用"
echo

mvn spring-boot:run 