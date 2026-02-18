#!/bin/bash

# DailyMenu GitHub 自动推送脚本
# 使用方法: 在 Git Bash 中运行此脚本

echo "==================================="
echo "DailyMenu GitHub 自动推送脚本"
echo "==================================="

# 检查当前目录
if [ ! -d ".git" ]; then
    echo "错误: 请在 DailyMenu 项目目录下运行此脚本"
    exit 1
fi

# 检查网络连接
echo ""
echo "检查网络连接..."
ping -c 3 github.com > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "⚠️  无法连接到 GitHub"
    echo ""
    echo "请尝试以下方法:"
    echo "1. 开启代理软件 (Clash/V2Ray/Shadowsocks)"
    echo "2. 然后运行:"
    echo "   git config --global http.proxy http://127.0.0.1:7890"
    echo "   git config --global https.proxy http://127.0.0.1:7890"
    echo "   git push origin main"
    echo ""
    echo "3. 或者使用 GitHub Desktop 推送"
    echo ""
    exit 1
fi

echo "✓ 网络连接正常"

# 显示将要推送的提交
echo ""
echo "将要推送的提交:"
git log --oneline origin/main..HEAD 2>/dev/null || git log --oneline -5

# 推送到 GitHub
echo ""
echo "正在推送到 GitHub..."
git push origin main

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ 推送成功！"
    echo ""
    echo "查看你的项目:"
    echo "https://github.com/mrzhang2/DailyMenu-"
else
    echo ""
    echo "❌ 推送失败"
    echo ""
    echo "请检查:"
    echo "1. 网络连接是否正常"
    echo "2. GitHub 账号权限是否正确"
    echo "3. 代理设置是否正确"
fi
