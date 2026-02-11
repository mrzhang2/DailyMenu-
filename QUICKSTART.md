# 🚀 快速开始 - 5分钟拿到 APK

## 最简单的方法：GitHub Actions（推荐）

### 第1步：准备代码（1分钟）
确认你的代码都在这个文件夹里了。

### 第2步：上传到 GitHub（2分钟）

**Windows 用户：**
1. 下载并安装 Git：https://git-scm.com/download/win
2. 双击运行 `upload-to-github.bat`
3. 按提示输入 GitHub 用户名和仓库名

**Mac/Linux 用户：**
```bash
cd DailyMenu
chmod +x upload-to-github.sh
./upload-to-github.sh
```

**不会用命令行？没关系！**
1. 打开浏览器访问 https://github.com
2. 注册/登录账号
3. 点击右上角 `+` → `New repository`
4. 仓库名填 `DailyMenu`，点击 `Create repository`
5. 点击 `uploading an existing file`
6. 把 `DailyMenu` 文件夹里的所有文件拖到网页上
7. 点击 `Commit changes`

### 第3步：自动构建（5-10分钟）
上传后会自动开始构建，你什么都不用做！

### 第4步：下载 APK（1分钟）
1. 在你的 GitHub 仓库页面点击 `Actions` 标签
2. 等待绿色 ✓ 出现（表示构建成功）
3. 点击进入完成的 workflow
4. 页面底部找到 `Artifacts` 
5. 点击 `DailyMenu-APK` 下载

## ✅ 完成！

把下载的 APK 传到手机上安装即可使用。

---

## 修改后重新构建

如果你想修改菜谱或界面：
1. 修改代码文件
2. 重新运行上传脚本，或手动推送到 GitHub
3. GitHub Actions 会自动重新构建
4. 下载新的 APK

---

## 需要帮助？

如果遇到问题：
1. 查看 `GITHUB_BUILD.md` 详细指南
2. 检查 GitHub Actions 页面上的错误日志
3. 确保代码文件完整上传

**现在就开始吧！🎉**