# `Auris`

## 项目简介

Auris 是一个简洁、稳定的音乐播放器项目，支持本地音乐播放、在线音频资源播放、个人歌单管理等功能。项目采用前后端分离架构，前端使用 Vue 3 + Vite，后端使用 Spring Boot + MyBatis Plus。

### 核心功能

- 🎵 **音乐播放**：支持本地音乐文件上传和在线音频资源播放
- 📋 **歌单管理**：创建、编辑、删除个人歌单，支持歌曲添加和排序
- 🎨 **用户界面**：现代化的 Web 界面，支持深色/浅色主题切换
- 🔐 **用户系统**：用户注册、登录、JWT 认证
- 📊 **播放历史**：记录用户播放历史
- 🎤 **歌词显示**：支持歌词显示功能

## 系统架构图

```
┌─────────────┐
│  前端应用    │  Vue 3 + Vite + Element Plus
│ (music-player)│
└──────┬──────┘
       │ HTTP/HTTPS
       │
┌──────▼──────┐
│  后端服务    │  Spring Boot + MyBatis Plus
│ (auris-server)│
└──────┬──────┘
       │
   ┌───┴───┬──────────┬──────────┐
   │       │          │          │
┌──▼──┐ ┌─▼──┐  ┌────▼────┐ ┌───▼───┐
│PostgreSQL│ │Redis│  │Aliyun OSS│ │其他服务│
└─────────┘ └────┘  └─────────┘ └───────┘
```

## 项目结构说明

```
HazeMusic/
├── backend/                    # 后端项目
│   ├── auris-common/          # 公共模块（工具类、常量、配置等）
│   ├── auris-pojo/            # 实体类模块（Entity、DTO、VO、Query等）
│   ├── auris-server/           # 服务模块（Controller、Service、Mapper等）
│   └── pom.xml                # Maven 父 POM
├── frontend/                   # 前端项目
│   ├── music-player/          # Vue 3 音乐播放器前端
│   │   ├── src/               # 源代码目录
│   │   │   ├── views/         # 页面组件
│   │   │   ├── components/    # 公共组件
│   │   │   ├── router/        # 路由配置
│   │   │   └── api.js         # API 接口
│   │   └── package.json       # 前端依赖配置
│   └── README.md              # 前端 README
├── documents/                  # 项目文档
│   ├── 需求文档.md            # 需求文档
│   ├── 技术架构.md            # 技术架构文档
│   ├── 数据库设计.md          # 数据库设计文档
│   └── 接口文档.md            # API 接口文档
└── README.md                   # 项目主 README
```

## 软件架构

### `Java`技术栈

#### 后端核心技术栈

| 技术                     | 说明                   | 版本          | 备注                                                         |
| ------------------------ | ---------------------- | ------------- | ------------------------------------------------------------ |
| `Spring Boot`            | Spring快速集成脚手架   | 2.6.13        | https://spring.io/projects/spring-boot                       |
| `MyBatis Plus`           | `MyBatis`的增强工具    | 3.5.3.1       | https://baomidou.com/                                        |
| `Lombok`                 | 实体类增加工具         | 1.18.20       | https://github.com/rzwitserloot/lombok                       |
| `Knife4j`                | 接口描述语言           | 3.0.2         | https://gitee.com/xiaoym/knife4j                             |
| `Nimbus JOSE JWT`        | `JSON Web Token`       | 9.40          | https://bitbucket.org/connect2id/nimbus-jose-jwt/wiki/Home   |
| `PostgreSQL Driver`      | PostgreSQL 数据库驱动  | 42.3.1        | https://jdbc.postgresql.org/                                 |
| `AspectJ`                | AOP 框架               | 1.9.4         | https://www.eclipse.org/aspectj/                             |
| `Fastjson`               | JSON 处理库            | 1.2.76        | https://github.com/alibaba/fastjson                          |
| `JJWT`                   | JWT 处理库             | 0.9.1         | https://github.com/jwtk/jjwt                                 |
| `Aliyun OSS SDK`         | 阿里云 OSS SDK         | 3.17.4        | https://github.com/aliyun/aliyun-oss-java-sdk               |


### 前端技术栈

#### 核心技术栈

| 技术           | 说明             | 版本                                                         | 备注                                 |
| -------------- | ---------------- | ------------------------------------------------------------ | ------------------------------------ |
| `Vue`          | 前端框架         | `3.5.24`                                                       | https://v3.vuejs.org/                |
| `Vue-Router`   | 路由框架         | `4.6.4`                                                       | https://next.router.vuejs.org/       |
| `Vite`         | 前端构建工具     | `7.2.5 (rolldown-vite)`                                       | https://vitejs.dev/                  |
| `Three.js`     | 3D 图形库        | `0.182.0`                                                    | https://threejs.org/                 |
| `ali-oss`      | 阿里云 OSS SDK   | `6.20.0`                                                     | https://github.com/ali-sdk/ali-oss   |



## 环境要求

> ### 开发工具
>
> | 工具            | 说明                  | 版本      | 备注                                                         |
> | --------------- | --------------------- | --------- | ------------------------------------------------------------ |
> | `Navicat`       | 数据库连接工具        | latest    | https://www.navicat.com.cn/                                  |
> | `RDM`           | `Redis`可视化管理工具 | latest    | https://github.com/uglide/RedisDesktopManager<br>https://gitee.com/qishibo/AnotherRedisDesktopManager |
> | `PowerDesigner` | 数据库设计工具        | 16.6      | http://powerdesigner.de/                                     |
> | `Axure`         | 原型设计工具          | 9         | https://www.axure.com/                                       |
> | `MindMaster`    | 思维导图设计工具      | latest    | http://www.edrawsoft.cn/mindmaster                           |
> | `Visio`         | 流程图绘制工具        | latest    | https://www.microsoft.com/zh-cn/microsoft-365/visio/flowchart-software |
> | `Apifox`        | `API`接口调试工具     | latest    | https://apifox.com/                                          |
> | `Mock.js`       | `API`接口模拟测试     | latest    | http://mockjs.com/                                           |
> | `Git`           | 项目版本管控工具      | latest    | https://git-scm.com/                                         |
> | `Codeup`        | 项目源码托管平台      | latest    | https://codeup.aliyun.com                                    |
> | `Projex`        | 开发过程管控平台      | latest    | https://devops.aliyun.com/projex                             |
> | `IDEA`          | `Java`开发`IDE`       | 2022.1.3+ | https://www.jetbrains.com/idea/download                      |
> | `Apache Maven`  | Maven 构建工具        | 3.6.3     | https://maven.apache.org/                                    |
> | `Docker Maven`  | Maven Docker插件      | 0.40.2    | https://dmp.fabric8.io/<br>https://github.com/fabric8io/docker-maven-plugin |
> | `VS`            | `C++`开发`IDE`        | 2022      | https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes |
> | `Cmake`         | `C++`跨平台编译       | latest    | https://cmake.org/cmake/help/latest/index.html               |
> | gtest           | 测试框架              | 1.14.0    | https://github.com/google/googletest                         |
> | `VS Code`       | 前端开发`IDE`         | latest    | https://code.visualstudio.com/Download                       |
>
> ### 开发环境
>
> | 依赖环境  | 版本       | 备注                      |
> | --------- | ---------- | ------------------------- |
> | `Windows` | 10+        | 操作系统                  |
> | `JDK`     | 17.0.9+    | https://www.injdk.cn/     |
> | `NodeJS`  | 20.15.0    | https://nodejs.org/zh-cn/ |
> | `NPM`     | 10.0.0+    | https://www.npmjs.com/    |
>
> ### 服务器环境
>
> | 依赖环境    | 版本                                                         | 备注                                                         |
> | ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
> | `Anolis OS` | `8.6GA`                                                      | https://openanolis.cn/anolisos                               |
> | `Docker`    | latest                                                       | https://www.docker.com/                                      |
> | `PostgreSQL` | 14.0+                                                      | https://www.postgresql.org/                                 |
> | `Redis`     | 6.2.7                                                        | https://redis.io/                                            |
> | `Nginx`     | latest                                                       | https://nginx.org/en/                                        |
>



## 部分功能预览图

> ![02](documents/00、preview-pic/02.png)
>
> ![03](documents/00、preview-pic/03.png)
>
> ![04](documents/00、preview-pic/04.png)
>
> ![01](documents/00、preview-pic/01.png)
>
> ![05](documents/00、preview-pic/05.png)
>
> ![06](documents/00、preview-pic/06.png)
>
> ![07](documents/00、preview-pic/07.png)
>



## 特别鸣谢

`Auris`的诞生离不开开源软件和社区的支持，感谢以下开源项目及项目维护者：

- `spring`：https://github.com/spring-projects
- `alibaba`：https://github.com/alibaba
- `mybatis`：https://github.com/mybatis/mybatis-3.git
- `mp`：https://github.com/baomidou
- `api`：https://gitee.com/xiaoym/knife4j
- `vue`：https://github.com/vuejs
- `ui`：https://github.com/ElemeFE

同时也感谢其他没有明确写出来的开源组件提供给与维护者。