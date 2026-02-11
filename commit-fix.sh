#!/bin/bash

# DailyMenu GitHub 构建修复提交脚本
# 使用方法: ./commit-fix.sh

echo "================================"
echo "DailyMenu GitHub 构建修复"
echo "================================"
echo ""

# 检查是否在 git 仓库中
if [ ! -d .git ]; then
    echo "❌ 错误: 当前目录不是 git 仓库"
    echo "请先初始化 git 仓库: git init"
    exit 1
fi

# 配置 git（如果尚未配置）
if [ -z "$(git config --global user.name)" ]; then
    echo "配置 Git 用户名..."
    git config --global user.name "Developer"
fi

if [ -z "$(git config --global user.email)" ]; then
    echo "配置 Git 邮箱..."
    git config --global user.email "developer@example.com"
fi

echo "📁 添加修复的文件到 git..."

# 添加新创建的文件
git add gradlew
git add .gitignore
git add .github/workflows/build.yml

# 显示将要提交的文件
echo ""
echo "📋 将要提交的文件:"
git status --short
echo ""

# 提交更改
echo "💾 提交更改..."
git commit -m "fix: 修复 GitHub Actions APK 构建问题

- 添加缺失的 gradlew (Linux/macOS 可执行文件)
- 优化 GitHub Actions 工作流配置
- 添加 Gradle 缓存加速构建
- 添加 .gitignore 文件排除不需要的文件
- 添加构建错误日志上传功能

现在 GitHub Actions 可以成功构建 APK 了！"

echo ""
echo "✅ 提交成功！"
echo ""
echo "🚀 推送到 GitHub:"
echo "   git push origin main"
echo ""
echo "📱 推送后，GitHub Actions 会自动运行构建"
echo "   查看构建状态: https://github.com/<你的用户名>/DailyMenu/actions"
echo ""
