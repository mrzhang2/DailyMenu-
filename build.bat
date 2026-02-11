@echo off
chcp 65001 >nul

echo ====================================
echo   每日菜单 (DailyMenu) 构建脚本
echo ====================================
echo.

REM 检查 Java
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到 Java，请先安装 JDK 17
    pause
    exit /b 1
)

echo 检查 Java 版本...
for /f "tokens=3" %%g in ('java -version 2^>^&1 ^| findstr /i "version"') do (
    echo Java 版本: %%g
)

REM 检查 Android SDK
if "%ANDROID_SDK_ROOT%"=="" (
    if "%ANDROID_HOME%"=="" (
        echo 警告: 未设置 ANDROID_SDK_ROOT 或 ANDROID_HOME 环境变量
        echo 请确保 Android SDK 已正确配置
    )
)

echo.
echo 清理之前的构建...
call gradlew.bat clean

if errorlevel 1 (
    echo 清理失败，继续构建...
)

echo.
echo 构建 Debug APK...
call gradlew.bat assembleDebug

if errorlevel 1 (
    echo.
    echo ====================================
    echo   构建失败！
    echo ====================================
    echo.
    echo 请检查错误信息并重试
    pause
    exit /b 1
)

echo.
echo ====================================
echo   构建成功！
echo ====================================
echo.
echo APK 文件位置:
echo   app\build\outputs\apk\debug\app-debug.apk
echo.
echo 安装到设备:
echo   adb install app\build\outputs\apk\debug\app-debug.apk
echo.

REM 检查是否有连接的设备
adb devices | findstr /V "List" | findstr "device" >nul
if not errorlevel 1 (
    set /p INSTALL="检测到已连接设备，是否安装? (y/n): "
    if /I "%INSTALL%"=="y" (
        adb install app\build\outputs\apk\debug\app-debug.apk
        if not errorlevel 1 (
            echo 安装成功！
            adb shell am start -n com.dailymenu/.MainActivity
        ) else (
            echo 安装失败，请检查设备连接
        )
    )
)

pause