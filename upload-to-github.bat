@echo off
chcp 65001 >nul

echo ========================================
echo   DailyMenu - GitHub 一键上传脚本
echo ========================================
echo.

REM 检查 Git
git --version >nul 2>&1
if errorlevel 1 (
    echo 请先安装 Git: https://git-scm.com/download/win
    pause
    exit /b 1
)

set /p USERNAME="请输入你的 GitHub 用户名: "
set /p REPO_NAME="请输入仓库名 (默认: DailyMenu): "
if "%REPO_NAME%"=="" set REPO_NAME=DailyMenu

echo.
echo 步骤 1: 初始化 Git 仓库...
git init

echo.
echo 步骤 2: 添加文件...
git add .

echo.
echo 步骤 3: 提交代码...
git commit -m "Initial commit"

echo.
echo 步骤 4: 连接远程仓库...
git remote add origin "https://github.com/%USERNAME%/%REPO_NAME%.git"

echo.
echo 步骤 5: 推送到 GitHub...
git branch -M main
git push -u origin main

echo.
echo ========================================
echo   上传完成！
echo ========================================
echo.
echo 接下来:
echo 1. 访问 https://github.com/%USERNAME%/%REPO_NAME%
echo 2. 点击 'Actions' 标签
echo 3. 等待构建完成（约5-10分钟）
echo 4. 下载 APK
echo.
pause