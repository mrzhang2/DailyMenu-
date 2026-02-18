# DailyMenu - GitHub 推送指南

## 🚀 快速推送方法

### 方法 1: 使用 Android Studio（推荐）

1. **打开 Android Studio**
2. **打开 DailyMenu 项目**
3. **点击菜单**: `Git` → `Push` (或按 `Ctrl+Shift+K`)
4. **点击 `Push` 按钮**

✅ 最简单的方法！

---

### 方法 2: 使用 GitHub Desktop

1. **下载 GitHub Desktop**: https://desktop.github.com
2. **登录你的 GitHub 账号**
3. **添加本地仓库**: `File` → `Add Local Repository`
4. **选择 DailyMenu 文件夹**
5. **点击 `Push origin` 按钮**

---

### 方法 3: 使用命令行（需开启代理）

```bash
# 1. 开启你的代理软件 (Clash/V2Ray等)

# 2. 配置 Git 代理
git config --global http.proxy http://127.0.0.1:7890
git config --global https.proxy http://127.0.0.1:7890

# 3. 推送
cd D:\work\project\DailyMenu
git push origin main

# 4. 推送完成后取消代理（可选）
git config --global --unset http.proxy
git config --global --unset https.proxy
```

---

### 方法 4: 使用 SSH 方式

```bash
# 1. 生成 SSH 密钥
ssh-keygen -t ed25519 -C "your_email@example.com"

# 2. 查看公钥
cat ~/.ssh/id_ed25519.pub

# 3. 复制公钥，添加到 GitHub: Settings → SSH and GPG keys → New SSH key

# 4. 修改远程仓库地址
cd D:\work\project\DailyMenu
git remote set-url origin git@github.com:mrzhang2/DailyMenu-.git

# 5. 推送
git push origin main
```

---

## 📊 当前状态

```
远程仓库: https://github.com/mrzhang2/DailyMenu-.git
分支: main
待推送提交: 15+ 个
文件数量: 74+ 个 Kotlin 文件
```

---

## 🔍 检查推送状态

推送成功后，访问：
**https://github.com/mrzhang2/DailyMenu-**

你应该能看到所有文件和提交记录。

---

## ⚠️ 常见问题

### 问题 1: Connection refused
**解决**: 开启代理软件，或使用 Android Studio/GitHub Desktop

### 问题 2: Authentication failed
**解决**: 
- 检查 GitHub 用户名密码
- 或使用 Personal Access Token: GitHub → Settings → Developer settings → Personal access tokens

### 问题 3: Remote rejected
**解决**: 
```bash
git pull origin main --rebase
git push origin main
```

---

## 📞 需要帮助?

如果以上方法都无法解决，请：
1. 检查网络连接
2. 尝试更换网络环境（手机热点）
3. 使用 GitHub Desktop 图形工具
