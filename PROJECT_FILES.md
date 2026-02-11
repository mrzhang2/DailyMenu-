# DailyMenu 项目文件说明

## 📁 文件清单

### 核心代码文件
```
app/src/main/java/com/dailymenu/
├── MainActivity.kt              # 应用主入口
├── DailyMenuApplication.kt      # 应用初始化
├── data/
│   ├── database/
│   │   ├── AppDatabase.kt       # Room 数据库
│   │   ├── RecipeDao.kt         # 数据访问对象
│   │   └── Converters.kt        # 类型转换器
│   ├── model/
│   │   ├── Recipe.kt            # 菜谱数据模型
│   │   └── WeatherResponse.kt   # 天气数据模型
│   ├── network/
│   │   ├── WeatherApi.kt        # 天气 API 接口
│   │   └── NetworkModule.kt     # 网络配置
│   └── repository/
│       ├── MenuRepository.kt    # 数据仓库
│       └── RecommendationEngine.kt # 推荐算法
├── ui/
│   ├── components/
│   │   └── CommonComponents.kt  # 通用 UI 组件
│   ├── screens/
│   │   ├── HomeScreen.kt        # 首页
│   │   ├── RecipeDetailScreen.kt # 菜谱详情
│   │   ├── FavoritesScreen.kt   # 收藏页面
│   │   └── SettingsScreen.kt    # 设置页面
│   ├── theme/
│   │   ├── Color.kt             # 颜色定义
│   │   ├── Theme.kt             # 主题配置
│   │   └── Type.kt              # 字体样式
│   └── viewmodel/
│       └── MenuViewModel.kt     # 视图模型 + 示例菜谱
└── worker/
    └── DailyMenuWorker.kt       # 每日推送任务
```

### 配置文件
```
app/
├── build.gradle                 # 应用构建配置
├── proguard-rules.pro          # 代码混淆规则
└── src/main/
    ├── AndroidManifest.xml     # 应用清单
    └── res/
        ├── values/
        │   ├── strings.xml     # 字符串资源
        │   ├── colors.xml      # 颜色资源
        │   └── themes.xml      # 主题资源
        └── drawable/
            └── ic_notification.xml # 通知图标

项目根目录/
├── build.gradle                # 项目构建配置
├── settings.gradle             # 项目设置
├── gradle.properties          # Gradle 配置
├── gradle/wrapper/
│   └── gradle-wrapper.properties # Gradle Wrapper 配置
└── .github/workflows/
    └── build.yml              # GitHub Actions 构建配置
```

### 辅助文件
```
├── README.md                   # 项目说明文档
├── QUICKSTART.md              # 快速开始指南 ⭐
├── GITHUB_BUILD.md            # GitHub 构建详细指南
├── build.sh                   # Linux/Mac 构建脚本
├── build.bat                  # Windows 构建脚本
├── upload-to-github.sh        # Linux/Mac 上传脚本
└── upload-to-github.bat       # Windows 上传脚本
```

## 🚀 快速开始（推荐）

### 最简单的 APK 获取方式：GitHub Actions

**只需要3步：**

1. **创建 GitHub 账号**
   - 访问 https://github.com
   - 点击 Sign up 注册（免费）

2. **上传代码到 GitHub**
   - Windows: 双击运行 `upload-to-github.bat`
   - Mac/Linux: 运行 `./upload-to-github.sh`
   - 或手动上传：看 `QUICKSTART.md`

3. **下载 APK**
   - 打开你的 GitHub 仓库
   - 点击 `Actions` 标签
   - 等待构建完成（绿色 ✓）
   - 下载 `DailyMenu-APK`

完成！把 APK 安装到手机即可。

## 📝 自定义内容

### 修改菜谱
编辑文件：`app/src/main/java/com/dailymenu/ui/viewmodel/MenuViewModel.kt`

找到 `createSampleRecipes()` 方法，添加你的菜谱：

```kotlin
Recipe(
    name = "你的菜谱名",
    description = "描述",
    category = RecipeCategory.CHINESE,
    mealType = MealType.LUNCH,
    ingredients = listOf("食材1", "食材2"),
    steps = listOf("步骤1", "步骤2"),
    cookingTime = 30,
    calories = 500,
    isHot = false,      // 热天推荐
    isCold = true,      // 冷天推荐
    isRainy = true,     // 雨天推荐
    isSunny = true,     // 晴天推荐
    season = Season.ALL_YEAR,
    tags = listOf("标签1", "标签2")
)
```

### 修改应用名称
编辑文件：`app/src/main/res/values/strings.xml`

```xml
<string name="app_name">你的应用名</string>
```

### 修改主题颜色
编辑文件：`app/src/main/java/com/dailymenu/ui/theme/Color.kt`

```kotlin
val PrimaryOrange = Color(0xFFFF8B5C)  // 主色调
val BackgroundCream = Color(0xFFFFF8F0) // 背景色
```

## 🔧 本地构建（可选）

如果你安装了 Android Studio：

1. 打开 Android Studio
2. 选择 `Open`，选择 `DailyMenu` 文件夹
3. 等待 Gradle 同步
4. 点击 `Build` → `Build APK`
5. APK 在 `app/build/outputs/apk/debug/`

## 📱 安装 APK

1. 把 APK 文件传到手机
2. 点击 APK 文件
3. 允许安装未知来源应用
4. 完成安装

## ❓ 常见问题

### Q: 上传脚本运行失败？
A: 确保已安装 Git。Windows 用户下载：https://git-scm.com/download/win

### Q: GitHub Actions 构建失败？
A: 检查代码文件是否完整上传，特别是 `app/build.gradle`

### Q: 下载的 APK 无法安装？
A: 安卓手机需要在设置中开启"允许安装未知来源应用"

### Q: 如何更新应用？
A: 修改代码后重新上传到 GitHub，GitHub Actions 会自动构建新版本

## 📞 需要帮助？

- 查看详细文档：`GITHUB_BUILD.md`
- GitHub Actions 文档：https://docs.github.com/cn/actions

---

**祝你使用愉快！🎉**