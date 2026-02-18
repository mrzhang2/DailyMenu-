#!/bin/bash
# GitHub 上传脚本 (DailyMenu)
# 使用方法: 在 Git Bash 或 WSL 中运行 ./push-to-github.sh

REPO_URL="https://github.com/mrzhang2/DailyMenu-.git"
BRANCH="main"

echo "=========================================="
echo "   DailyMenu GitHub 上传脚本"
echo "=========================================="
echo ""

# 检查是否有更改要提交
if [ -z "$(git status --porcelain)" ]; then
    echo "✓ 工作目录干净，没有要提交的更改"
else
    echo "发现未提交的更改，正在添加..."
    git add .
    echo "请输入提交信息 (直接回车使用默认信息 'update'):"
    read msg
    if [ -z "$msg" ]; then
        msg="update"
    fi
    git commit -m "$msg"
    echo "✓ 更改已提交"
fi

echo ""
echo "正在推送到 GitHub..."
echo "仓库: $REPO_URL"
echo "分支: $BRANCH"
echo ""

# 尝试推送
git push origin $BRANCH

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "   ✓ 推送成功!"
    echo "=========================================="
else
    echo ""
    echo "=========================================="
    echo "   ✗ 推送失败"
    echo "=========================================="
    echo ""
    echo "可能的原因:"
    echo "  1. 网络连接问题"
    echo "  2. 需要身份验证 (运行: git config --global credential.helper cache)"
    echo "  3. 没有写入权限"
    echo ""
    echo "备用方案:"
    echo "  - 使用 GitHub Desktop"
    echo "  - 使用 SSH 协议: git@github.com:mrzhang2/DailyMenu-.git"
    echo "  - 检查代理设置"
fi
