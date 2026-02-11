#!/bin/bash

# DailyMenu 构建脚本

echo "===================================="
echo "  每日菜单 (DailyMenu) 构建脚本"
echo "===================================="
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "错误: 未找到 Java，请先安装 JDK 17"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "Java 版本: $JAVA_VERSION"

# 检查 Android SDK
if [ -z "$ANDROID_SDK_ROOT" ] && [ -z "$ANDROID_HOME" ]; then
    echo "警告: 未设置 ANDROID_SDK_ROOT 或 ANDROID_HOME 环境变量"
    echo "请确保 Android SDK 已正确配置"
fi

# 清理之前的构建
echo ""
echo "清理之前的构建..."
./gradlew clean

# 构建 Debug APK
echo ""
echo "构建 Debug APK..."
./gradlew assembleDebug

# 检查构建结果
if [ $? -eq 0 ]; then
    echo ""
    echo "===================================="
    echo "  构建成功！"
    echo "===================================="
    echo ""
    echo "APK 文件位置:"
    echo "  app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    echo "安装到设备:"
    echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
    echo ""
    
    # 如果连接了设备，询问是否安装
    if adb devices | grep -q "device$"; then
        read -p "检测到已连接设备，是否安装? (y/n): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            adb install app/build/outputs/apk/debug/app-debug.apk
            if [ $? -eq 0 ]; then
                echo "安装成功！"
                adb shell am start -n com.dailymenu/.MainActivity
            else
                echo "安装失败，请检查设备连接"
            fi
        fi
    fi
else
    echo ""
    echo "===================================="
    echo "  构建失败！"
    echo "===================================="
    echo ""
    echo "请检查错误信息并重试"
    exit 1
fi