# DailyMenu v2.0 - 打包指南

## 📦 项目概述

**项目名称**: DailyMenu 每日菜单  
**版本**: v2.0.0  
**完成日期**: 2026-02-17  

## 🎯 功能特性

### ✅ 已实现功能

#### MVP 阶段
- ✅ 智能推荐（天气感知）
- ✅ 微信登录
- ✅ 图文教程（步骤图片、计时器、食材计算器）
- ✅ 用户个人中心
- ✅ 收藏功能
- ✅ 底部导航

#### 预算功能
- ✅ 每日预算设置（20-200元）
- ✅ 预算分配（早餐20%/午餐40%/晚餐40%）
- ✅ 价格显示和预算提醒

#### Phase 2 - 核心增强
- ✅ 视频教学（ExoPlayer）
- ✅ 评论系统（支持回复、点赞）
- ✅ 作品分享（晒图）
- ✅ 学习进度追踪

#### Phase 3 - 完善功能
- ✅ 发现页面（搜索、分类）
- ✅ 消息通知
- ✅ 设置页面（推送、播放、隐私）

## 📁 项目文件统计

- **Kotlin 文件**: 74+ 个
- **页面数量**: 10+ 个
- **UI 组件**: 25+ 个
- **数据库表**: 8 个
- **总代码行数**: 约 5000+ 行

## 🛠️ 技术栈

- **语言**: Kotlin 1.9.0
- **UI 框架**: Jetpack Compose
- **架构**: MVVM + Clean Architecture
- **依赖注入**: Hilt 2.48
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **图片加载**: Coil
- **视频播放**: Media3 ExoPlayer
- **本地存储**: DataStore
- **认证**: WeChat SDK

## 📱 打包步骤

### 前提条件

1. **安装 Android Studio** (2023.1.1 或更高版本)
2. **安装 JDK 17**
3. **配置 Android SDK** (API 24-34)

### 打包流程

#### 方法 1: 使用 Android Studio

1. **打开项目**
   ```
   File → Open → 选择 DailyMenu 文件夹
   ```

2. **等待 Gradle 同步**
   - 首次打开会自动同步
   - 约需 3-5 分钟

3. **构建 APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

4. **获取 APK 文件**
   - Debug: `app/build/outputs/apk/debug/app-debug.apk`
   - Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

#### 方法 2: 使用命令行

```bash
# 进入项目目录
cd DailyMenu

# 构建 Debug APK
./gradlew assembleDebug

# 构建 Release APK
./gradlew assembleRelease
```

### 签名 Release APK

```bash
# 生成密钥库
keytool -genkey -v -keystore dailymenu.keystore -alias dailymenu -keyalg RSA -validity 10000

# 签名 APK
jarsigner -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore dailymenu.keystore app-release-unsigned.apk dailymenu

# 优化 APK
zipalign -v 4 app-release-unsigned.apk DailyMenu-v2.0.0.apk
```

## 🔧 常见问题

### 问题 1: Gradle 同步失败

**解决方案**:
```bash
# 清除 Gradle 缓存
./gradlew clean

# 重新同步
./gradlew build
```

### 问题 2: Java 版本不匹配

**解决方案**:
1. 打开 `File → Settings → Build, Execution, Deployment → Build Tools → Gradle`
2. 设置 Gradle JDK 为 JDK 17

### 问题 3: 依赖下载失败

**解决方案**:
1. 检查网络连接
2. 添加阿里云 Maven 镜像到 `build.gradle`:
   ```gradle
   maven { url 'https://maven.aliyun.com/repository/public' }
   ```

## 📋 发布前检查清单

- [ ] 所有功能测试通过
- [ ] 微信 AppID 已配置
- [ ] 后端 API 地址已配置
- [ ] 应用图标已设置
- [ ] 应用名称已设置
- [ ] 版本号已更新
- [ ] 权限声明完整
- [ ] 隐私政策链接已添加

## 📄 应用权限

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.CAMERA" />
```

## 🚀 部署建议

### 测试环境
- 使用 Debug APK 进行内部测试
- 邀请 5-10 名测试用户

### 生产环境
1. **Google Play 商店**
   - 创建开发者账号
   - 上传 Release APK
   - 填写应用信息

2. **国内应用商店**
   - 华为应用市场
   - 小米应用商店
   - OPPO/vivo 应用商店

## 📞 技术支持

如有问题，请查看：
- README.md - 项目说明
- CHANGELOG.md - 变更日志
- docs/ - 文档目录

---

**DailyMenu Team**  
**版本**: v2.0.0  
**日期**: 2026-02-17
