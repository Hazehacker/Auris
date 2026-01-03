# `Auris` - 后端项目

## 项目简介

Auris 后端是一个基于 Spring Boot 2.6.13 构建的 RESTful API 服务，提供音乐播放器所需的后端功能，包括用户管理、音乐管理、歌单管理、播放历史等核心功能。

### 核心功能

- 👤 **用户管理**：用户注册、登录、JWT 认证
- 🎵 **音乐管理**：音乐文件上传、元数据管理、音乐信息查询
- 📋 **歌单管理**：创建、编辑、删除歌单，歌曲添加和排序
- 📊 **播放历史**：记录用户播放历史
- 🎤 **歌词管理**：歌词上传和管理
- ☁️ **文件存储**：集成阿里云 OSS 进行文件存储

## 系统架构图

```
┌─────────────────────────────────────┐
│      Spring Boot 应用层             │
│  ┌──────────┐  ┌──────────┐        │
│  │Controller│  │ Service  │        │
│  └────┬─────┘  └────┬─────┘        │
│       │             │               │
│  ┌────▼─────────────▼─────┐        │
│  │    MyBatis Plus         │        │
│  └────┬────────────────────┘        │
└───────┼─────────────────────────────┘
        │
   ┌────┴────┬──────────┬──────────┐
   │         │          │          │
┌──▼──┐  ┌──▼──┐  ┌────▼────┐ ┌───▼───┐
│PostgreSQL│ │Redis│  │Aliyun OSS│ │其他服务│
└─────────┘ └────┘  └─────────┘ └───────┘
```

## 项目结构说明

```
backend/
├── auris-common/              # 公共模块
│   └── src/main/java/top/hazenix/auris/
│       ├── constant/          # 常量定义
│       ├── context/           # 上下文（如用户上下文）
│       ├── enumeration/       # 枚举类
│       ├── properties/        # 配置属性类
│       ├── result/            # 统一返回结果
│       └── utils/             # 工具类（JWT、OSS、HTTP等）
├── auris-pojo/                # 实体类模块
│   └── src/main/java/top/hazenix/auris/
│       ├── dto/               # 数据传输对象
│       ├── entity/            # 实体类（User、Track、Playlist等）
│       ├── handler/           # 异常处理器
│       ├── query/             # 查询对象
│       └── vo/                # 视图对象
├── auris-server/              # 服务模块
│   └── src/main/java/top/hazenix/auris/
│       ├── annotation/        # 自定义注解
│       ├── aspect/            # AOP 切面
│       ├── config/            # 配置类（CORS、WebMvc等）
│       ├── controller/        # 控制器层
│       │   └── user/          # 用户端接口
│       ├── interceptor/       # 拦截器（JWT等）
│       ├── mapper/            # MyBatis Mapper 接口
│       └── service/           # 服务层
│           └── impl/          # 服务实现类
└── pom.xml                     # Maven 父 POM
```

## 软件架构

### `Java`技术栈

#### 后端核心技术栈

版本匹配参考：

https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E

| 技术                     | 说明                   | 版本            | 备注                                                         |
| ------------------------ | ---------------------- |---------------| ------------------------------------------------------------ |
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



## 环境要求

### 开发工具

| 工具            | 说明                  | 版本      | 备注                                                         |
| --------------- | --------------------- | --------- | ------------------------------------------------------------ |
| `Navicat`       | 数据库连接工具        | latest    | https://www.navicat.com.cn/                                  |
| `RDM`           | `Redis`可视化管理工具 | latest    | https://github.com/uglide/RedisDesktopManager<br>https://gitee.com/qishibo/AnotherRedisDesktopManager |
| `PowerDesigner` | 数据库设计工具        | 16.6      | http://powerdesigner.de/                                     |
| `Axure`         | 原型设计工具          | 9         | https://www.axure.com/                                       |
| `MindMaster`    | 思维导图设计工具      | latest    | http://www.edrawsoft.cn/mindmaster                           |
| `Visio`         | 流程图绘制工具        | latest    | https://www.microsoft.com/zh-cn/microsoft-365/visio/flowchart-software |
| `Apifox`        | `API`接口调试工具     | latest    | https://apifox.com/                                          |
| `Mock.js`       | `API`接口模拟测试     | latest    | http://mockjs.com/                                           |
| `Git`           | 项目版本管控工具      | latest    | https://git-scm.com/                                         |
| `Codeup`        | 项目源码托管平台      | latest    | https://codeup.aliyun.com                                    |
| `Projex`        | 开发过程管控平台      | latest    | https://devops.aliyun.com/projex                             |
| `IDEA`          | `Java`开发`IDE`       | 2022.1.3+ | https://www.jetbrains.com/idea/download                      |
| `Apache Maven`  | Maven 构建工具        | 3.6.3     | https://maven.apache.org/                                    |
| `Docker Maven`  | Maven Docker插件      | 0.40.2    | https://dmp.fabric8.io/<br>https://github.com/fabric8io/docker-maven-plugin |
| `VS`            | `C++`开发`IDE`        | 2022      | https://learn.microsoft.com/en-us/visualstudio/releases/2022/release-notes |
| `Cmake`         | `C++`跨平台编译       | latest    | https://cmake.org/cmake/help/latest/index.html               |
| gtest           | 测试框架              | 1.14.0    | https://github.com/google/googletest                         |
| `VS Code`       | 前端开发`IDE`         | latest    | https://code.visualstudio.com/Download                       |

### 开发环境

| 依赖环境  | 版本      | 备注                      |
| --------- |---------| ------------------------- |
| `Windows` | 10+     | 操作系统                  |
| `JDK`     | 17.0.9  | https://www.injdk.cn/     |
| `NodeJS`  | 20.15.0 | https://nodejs.org/zh-cn/ |
| `NPM`     | 8.19.2  | https://www.npmjs.com/    |

### 服务器环境

> | 依赖环境    | 版本                                                         | 备注                                                         |
> | ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
> | `Anolis OS` | `8.6GA`                                                      | https://openanolis.cn/anolisos                               |
> | `Docker`    | latest                                                       | https://www.docker.com/                                      |
> | `PostgreSQL` | 14.0+                                                      | https://www.postgresql.org/                                 |
> | `Redis`     | 6.2.7                                                        | https://redis.io/                                            |
> | `Nginx`     | latest                                                       | https://nginx.org/en/                                        |
>

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- PostgreSQL 14.0+
- Redis 6.2+

### 配置说明

1. 修改 `auris-server/src/main/resources/application.yml` 中的数据库和 Redis 配置
2. 配置阿里云 OSS 相关参数（用于文件存储）
3. 配置 JWT 相关参数

### 运行项目

xxApplication启动类  或

```bash
# 编译项目
mvn clean package

# 运行服务
cd auris-server
java -jar target/auris-server-1.0.0-SNAPSHOT.jar
```

### API 文档

启动服务后，访问 Swagger 文档：
- 开发环境：http://localhost:8080/doc.html
- 生产环境：https://auris.hazenix.top/doc.html

## 部分功能预览图

> （预览图位于项目根目录 `documents/` 目录下，如有需要可添加预览图）
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