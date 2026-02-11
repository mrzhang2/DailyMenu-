# 每日菜单 (DailyMenu)

一款根据天气智能推荐每日三餐的 Android 应用。

## 功能特点

- **智能推荐**：根据当前天气、温度、季节自动推荐最合适的菜谱
- **自动天气**：自动获取当前位置天气，也可手动输入
- **每日推送**：每天早上8点自动推送今日菜单
- **温馨界面**：暖色调美食风格设计，使用舒适
- **本地数据**：内置200+道精选菜谱，无需联网即可使用
- **收藏功能**：收藏喜欢的菜谱，方便查看

## 推荐算法

应用综合考虑以下因素推荐菜谱：
- **温度**：高温推荐清凉食物，低温推荐温暖食物
- **天气**：雨天推荐汤类，晴天推荐轻食
- **季节**：根据春夏秋冬推荐时令菜
- **营养均衡**：早中晚餐营养搭配

## 技术栈

- **语言**：Kotlin
- **UI框架**：Jetpack Compose
- **架构**：MVVM
- **数据库**：Room
- **网络**：Retrofit + OkHttp
- **定时任务**：WorkManager
- **依赖注入**：无（纯原生实现）

## 项目结构

```
DailyMenu/
├── app/src/main/java/com/dailymenu/
│   ├── data/
│   │   ├── database/      # Room数据库
│   │   ├── model/         # 数据模型
│   │   ├── network/       # 网络请求
│   │   └── repository/    # 数据仓库
│   ├── ui/
│   │   ├── components/    # UI组件
│   │   ├── screens/       # 界面
│   │   ├── theme/         # 主题
│   │   └── viewmodel/     # 视图模型
│   ├── worker/            # 后台任务
│   ├── MainActivity.kt    # 主Activity
│   └── DailyMenuApplication.kt
├── app/build.gradle       # 应用构建配置
└── build.gradle          # 项目构建配置
```

## 构建步骤

### 前提条件

1. 安装 Android Studio (2023.1.1 或更高版本)
2. 安装 JDK 17
3. Android SDK (API 24-34)

### 构建 APK

#### 方法1：使用 Android Studio

1. 打开 Android Studio
2. 选择 "Open" 并导入此项目
3. 等待 Gradle 同步完成
4. 选择菜单栏 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
5. APK 将生成在 `app/build/outputs/apk/debug/app-debug.apk`

#### 方法2：使用命令行

```bash
# 进入项目目录
cd DailyMenu

# 使用 Gradle Wrapper 构建
./gradlew assembleDebug

# 或者在 Windows 上
gradlew.bat assembleDebug
```

构建完成后，APK 文件位于：
- Debug 版本：`app/build/outputs/apk/debug/app-debug.apk`
- Release 版本：`app/build/outputs/apk/release/app-release-unsigned.apk`

### 安装 APK

1. 启用手机的 "开发者选项" 和 "USB 调试"
2. 连接手机到电脑
3. 使用 ADB 安装：
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
   
   或者直接复制 APK 到手机，在文件管理器中点击安装

## 使用说明

### 首次使用

1. 打开应用，允许位置权限（用于获取天气）
2. 应用会自动获取当前天气并生成今日菜单
3. 查看早中晚三餐推荐，点击可查看详细做法

### 手动设置天气

如果自动获取天气失败：
1. 点击天气卡片上的编辑图标
2. 输入温度和选择天气状况
3. 点击确定，菜单会重新生成

### 收藏菜谱

1. 在首页点击任意餐的爱心图标
2. 或在菜谱详情页点击收藏按钮
3. 收藏的可在"我的收藏"页面查看

### 每日推送

应用默认每天早上8点推送通知：
- 显示今日三餐推荐
- 点击通知打开应用查看详情
- 可在设置中关闭推送

## 配置天气 API（可选）

默认使用和风天气免费 API，但 API Key 需要自行申请：

1. 访问 [和风天气](https://dev.qweather.com/)
2. 注册账号并创建应用获取 API Key
3. 修改 `MenuRepository.kt` 中的 `DEFAULT_API_KEY`

如果不配置，应用仍可使用手动天气输入功能。

## 自定义菜谱

可以在 `MenuViewModel.kt` 中的 `createSampleRecipes()` 方法添加自己的菜谱：

```kotlin
Recipe(
    name = "菜谱名称",
    description = "描述",
    category = RecipeCategory.CHINESE,
    mealType = MealType.LUNCH,
    ingredients = listOf("食材1", "食材2"),
    steps = listOf("步骤1", "步骤2"),
    cookingTime = 30,
    calories = 500,
    isHot = false,      // 是否适合热天
    isCold = true,      // 是否适合冷天
    isRainy = true,     // 是否适合雨天
    isSunny = true,     // 是否适合晴天
    season = Season.ALL_YEAR,
    tags = listOf("标签1", "标签2")
)
```

## 注意事项

1. **天气 API**：首次使用建议配置和风天气 API Key 以获得更好体验
2. **位置权限**：需要位置权限才能自动获取天气
3. **通知权限**：需要通知权限才能接收每日推送
4. **网络连接**：自动获取天气需要网络连接

## 开源协议

MIT License

## 更新日志

### v1.0.0
- 初始版本发布
- 实现天气获取和智能推荐
- 支持早中晚三餐推荐
- 支持菜谱收藏
- 支持每日推送通知

---

祝你用餐愉快！🍽️