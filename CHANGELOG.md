# Changelog

All notable changes to the DailyMenu project will be documented in this file.

## [v2.0.0] - 2026-02-18

### 新增功能 (Added)

#### 用户系统
- 微信登录集成 - 支持微信授权登录
- 用户个人中心 - 显示用户统计信息、成就和设置
- Token 管理和自动刷新机制

#### 视频功能
- 视频播放器组件 - 使用 ExoPlayer (Media3) 实现
- 视频章节导航 - 支持按章节跳转
- 视频封面组件 - 显示视频缩略图
- 学习进度追踪 - 记录视频观看进度

#### 社交功能
- 评论系统 - 支持菜谱评论、评分
- 评论列表组件 - 显示所有评论
- 评论输入组件 - 发表新评论
- 星级评分组件 - 菜谱评分功能

#### 发现页面
- 搜索功能 - 按关键词搜索菜谱
- 分类筛选 - 按类别浏览菜谱
- 热门推荐 - 显示热门菜谱
- 菜谱网格展示

#### 收藏功能
- 收藏管理 - 支持分类筛选和取消收藏
- 收藏数据持久化

#### 图文教程增强
- 步骤计时器 - 为烹饪步骤设置倒计时
- 食材用量计算器 - 支持按人数调整用量
- 步骤图片展示
- 烹饪小贴士显示

#### 设置功能
- 通知设置 - 管理推送通知
- 个人资料管理
- 主题和显示设置
- 隐私设置

#### 预算管理
- 预算设置对话框
- 预算显示卡片
- 预算调整滑块
- 菜谱成本字段

#### 作品分享
- 作品展示页面
- 作品网格展示组件
- 用户作品管理

### 技术改进 (Changed)

#### 架构升级
- 引入 Hilt 依赖注入框架
- 添加 Coil 图片加载库
- 添加 DataStore 偏好设置存储
- 添加 Security Crypto 加密存储
- 创建完整的 DI 模块体系

#### 数据模型扩展
- 扩展 Recipe 模型（添加视频、图文、社交字段）
- 创建 User 用户模型
- 创建 StepImage 步骤图片模型
- 创建 Comment 评论模型
- 创建 Work 作品模型
- 创建 LearningProgress 学习进度模型
- 创建 Notification 通知模型
- 定义 DifficultyLevel 难度等级枚举
- 定义 MemberLevel 会员等级枚举
- 数据库 Schema 升级到 v2

#### 导航重构
- 创建 Screen 路由定义
- 创建 BottomNavItem 底部导航项
- 创建 NavGraph 导航图
- 重构为带底部导航的主页面结构

#### 第三方服务集成
- 微信 SDK 集成
- ExoPlayer (Media3) 视频播放
- 位置服务集成

### 数据库 (Database)
- Room 数据库支持多表关联
- 添加 TypeConverters 处理复杂类型
- 支持用户数据、评论、作品、学习进度等数据持久化

### 页面 (UI Screens)
- LoginScreen - 登录页面
- MainScreen - 带底部导航的主页面
- HomeScreen - 首页（今日推荐）
- DiscoverScreen - 发现页面
- RecipeDetailScreen - 菜谱详情页面
- FavoritesScreen - 收藏页面
- ProfileScreen - 个人中心页面
- WorkScreen - 作品展示页面
- SettingsScreen - 设置页面
- NotificationScreen - 通知页面

### 文档 (Documentation)
- 添加详细需求规格说明书 (REQUIREMENTS.md)
- 添加 MVP 完成总结文档 (docs/MVP-COMPLETION.md)
- 添加快速开始指南 (QUICKSTART.md)
- 添加项目文件说明 (PROJECT_FILES.md)
- 添加 GitHub Actions 构建说明 (GITHUB_BUILD.md)

### 项目统计
- 总文件数: 74+ 个 Kotlin 文件
- 总代码行数: 约 4000+ 行
- 组件数: 25+ 个可复用 UI 组件
- 数据表: 8+ 个数据库表

## [v1.0.0] - 初始版本

### 功能
- 智能推荐：根据天气、温度、季节推荐菜谱
- 自动天气获取
- 每日推送：早上8点推送今日菜单
- 基础菜谱展示
- 收藏功能
- 本地数据：内置200+精选菜谱

### 技术栈
- Kotlin
- Jetpack Compose
- MVVM 架构
- Room 数据库
- Retrofit + OkHttp
- WorkManager

---

## 未来计划 (Upcoming)

### Phase 3 计划
- 视频上传和管理（后端对接）
- 完整的社交分享功能（分享到微信/朋友圈）
- 个性化推荐算法优化
- 搜索功能增强（语音搜索、智能联想）
- 后端 API 全面对接

### 长期目标
- 直播烹饪功能
- 会员体系和增值服务
- 创作者收益体系
- AI 烹饪助手
