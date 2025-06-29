# Online Judge 系统（基于 Spring Boot + Vue + Docker）

这是一个全栈式在线判题系统（OJ），支持用户注册、题目管理、代码提交与运行历史查询。使用了 Vue3 + Vite + ElementPlus 构建前端，Spring Boot + MyBatis 构建后端，并通过 Docker Compose 实现一键部署。

---

## 🌐 项目结构

```

onlinejudge/
├── boot/        # 后端 Spring Boot 项目
├── Vue/         # 前端 Vue3 项目
├── docker-compose.yml

````

---

## 🚀 技术栈

| 层级 | 技术                     |
|------|--------------------------|
| 前端 | Vue3, Vite, Element Plus |
| 后端 | Spring Boot, MyBatis     |
| 数据库 | MySQL 8.0             |
| 部署 | Docker, Docker Compose  |

---

## 📦 功能亮点

- 用户注册与登录（JWT 鉴权）
- Markdown 编辑器支持（题目说明）
- 支持代码编辑、提交、运行结果展示
- 代码运行历史记录查看
- 响应式 UI，适配不同设备
- 一键部署，开箱即用

---

## 🗂️ 新手入门

1. 你需要有一个 GitHub 账号，并登录 [GitHub 官网](https://github.com)。
2. 在本地生成SSH密钥。
```bash
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"
```
3. 将生成的公钥内容（id_rsa.pub）添加到你的 GitHub 账户的 SSH keys。
4. 测试 SSH 连接：
```bash
ssh -T git@github.com
```
5. 拉取项目 
```bash
git clone https://github.com/CUMTXH/online-judge
```
6. 如果需要推送，记得在本地绑定账号
```bash
git config --global user.email "you@example.com"
git config --global user.name "Your Name"
```
7. 添加所有文件并提交：
```bash
git add .
git commit -m "init"
```
8. 推送到 GitHub（首次推送建议加 -u 参数）：
```bash
git push -u origin dev
```

---

## 🧱 快速启动（推荐使用 Docker Compose）

### 1. 克隆项目

```bash
git clone https://github.com/CUMTXH/online-judge
cd onlinejudge
````

### 2. 下载docker并运行容器（首次构建可能较慢）

```bash
sudo snap install docker 
sudo docker-compose up -d --build
```

首次构建会自动打包前端、后端并初始化数据库。

---

## 🔗 默认访问地址

* 前端页面：http\://<你的服务器IP>:8080
* 后端 API 接口：http\://<你的服务器IP>:8081
* 数据库连接：MySQL 8.0（用户/密码见下方）

---

## 🛠️ 数据库配置

默认配置如下（可在 `docker-compose.yml` 中修改）：

```env
MYSQL_ROOT_PASSWORD=rootpass
MYSQL_DATABASE=note
MYSQL_USER=ojuser
MYSQL_PASSWORD=ojpassword
```

---

## 📁 本地开发指南（可选）

### 启动后端（Java 环境需预先安装）

```bash
cd boot
./mvnw spring-boot:run
```

### 启动前端（Node.js 环境需预先安装）

```bash
cd Vue
npm install
npm run dev
```

---

## 🧠 常见问题

### ❓访问不了 [http://localhost:8080？](http://localhost:8080？)

* 端口被占用？可以在 `docker-compose.yml` 里修改端口。
* 防火墙没开放？检查服务器防火墙（如 `ufw allow 8080`）。

### ❓页面空白？

* 检查 `nginx.conf` 是否正确配置了 `try_files`。
* `vite.config.ts` 中应设置 `base: './'` 防止路径解析错误。

---

## 📌 TODO（可持续完善）

* 在线编译器多语言支持
* 题库分页与标签筛选
* 用户排行榜 & 竞赛系统
* 文件上传支持
* Admin 后台管理页面

---

## 🧑‍💻 作者

* **开发者**: Dezoist,seekerzhz
* **部署环境**: Ubuntu 22.04, Docker Engine 24+

---

## 📜 License

MIT License
