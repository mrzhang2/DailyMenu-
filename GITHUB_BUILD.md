# 在线构建指南 (GitHub Actions)

使用 GitHub Actions 免费自动构建 APK，无需安装 Android Studio！

## 步骤

### 1. 创建 GitHub 账号
- 访问 https://github.com
- 注册账号（免费）

### 2. 创建仓库
1. 点击右上角 `+` → `New repository`
2. 仓库名：`DailyMenu`
3. 选择 `Public`（公开）或 `Private`（私有）
4. 点击 `Create repository`

### 3. 上传代码

**方法A：使用 Git 命令行**
```bash
# 进入项目目录
cd DailyMenu

# 初始化 Git 仓库
git init

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit"

# 连接远程仓库（替换 YOUR_USERNAME 为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/DailyMenu.git

# 推送代码
git branch -M main
git push -u origin main
```

**方法B：直接上传（更简单）**
1. 在仓库页面点击 `uploading an existing file`
2. 把项目文件夹里的所有文件拖到网页上
3. 点击 `Commit changes`

### 4. 触发构建
上传代码后会自动触发构建，或者：
1. 进入仓库页面
2. 点击上方 `Actions` 标签
3. 点击左侧 `Build APK`
4. 点击右侧 `Run workflow` → `Run workflow`

### 5. 下载 APK
1. 等待构建完成（约 5-10 分钟）
2. 点击 `Actions` 标签查看最新运行记录
3. 点击已完成的 workflow
4. 在页面底部 `Artifacts` 区域下载：
   - `DailyMenu-APK` - 调试版（推荐安装）
   - `DailyMenu-Release-APK` - 发布版（未签名）

## 自动构建

每次你推送代码到 GitHub，都会自动构建新的 APK。你可以：
- 修改菜谱
- 更改主题颜色
- 更新应用名称

修改后推送到 GitHub，新的 APK 会自动生成！

## 常见问题

### Q: 构建失败怎么办？
A: 点击失败的 workflow → 查看日志，通常是依赖下载问题，重新运行即可。

### Q: 下载的 APK 无法安装？
A: 
1. 确保下载的是 `DailyMenu-APK`（不是 Release 版）
2. 安卓手机需要在设置中开启"允许安装未知来源应用"

### Q: 如何修改应用配置？
A: 修改 `app/build.gradle` 文件中的：
- `applicationId` - 应用包名
- `versionName` - 版本号
- `versionCode` - 版本代码

### Q: 如何自定义菜谱？
A: 修改 `app/src/main/java/com/dailymenu/ui/viewmodel/MenuViewModel.kt` 中的 `createSampleRecipes()` 方法。

### Q: 构建太慢？
A: 首次构建较慢（需要下载依赖），后续会快很多。

## 相关链接

- GitHub Actions 文档：https://docs.github.com/cn/actions
- Android 构建指南：https://developer.android.com/studio/build

---

祝你使用愉快！🎉