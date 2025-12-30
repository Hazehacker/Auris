# `name`



## 系统架构图



## 项目结构说明


## 软件架构



### 前端技术栈

#### 核心技术栈

参考

| 技术           | 说明             | 版本                                                         | 备注                                 |
| -------------- | ---------------- | ------------------------------------------------------------ | ------------------------------------ |
| `Vue`          | 前端框架         | `v3.x`                                                       | https://v3.vuejs.org/                |
| `Vue-Router`   | 路由框架         | `v4.x`                                                       | https://next.router.vuejs.org/       |
| `Pinia`        | 全局状态管理框架 | `v2.x`                                                       | https://pinia.vuejs.org/             |
| `Axios`        | HTTP中间件       | [v1.7.2](https://github.com/axios/axios/releases/tag/v1.7.2) | https://github.com/axios/axios       |
| `Element-Plus` | 前端`UI`框架     | `latest`                                                     | https://element-plus.gitee.io/zh-CN/ |

#### 扩展技术栈

| 技术                 | 说明          | 版本   | 备注                                                         |
| -------------------- | ------------- | ------ | ------------------------------------------------------------ |
| `ECharts`            | 图表框架      | latest | [`Apache ECharts`](https://echarts.apache.org/handbook/zh/get-started/) |
| `AJ-Captcha`         | 验证码插件    | 1.3.0  | https://ajcaptcha.beliefteam.cn/captcha-doc/                 |
| `SheetJS`            | 电子表格插件  | 0.20.2 | https://docs.sheetjs.com/docs/<br>https://docs.sheetjs.com/docs/demos/frontend/vue |
| `vue-plugin-hiprint` | 打印插件      | 0.0.56 | https://gitee.com/CcSimple/vue-plugin-hiprint                |
| `wangEditor`         | 富文本编辑器  | v5     | https://www.wangeditor.com/v5/                               |
| `pdfobject`          | `pdf`预览插件 | 2.3.0  | https://github.com/pipwerks/PDFObject                        |
| `Vitest`             | 测试框架      | 1.6.0  | https://cn.vitest.dev/<br>https://cn.vuejs.org/guide/scaling-up/testing.html |
| `pinyin-pro`         | 汉字转拼音库  | latest | https://pinyin-pro.cn/                                       |

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

| 依赖环境  | 版本 | 备注                      |
| --------- | ---- | ------------------------- |
| `Windows` | 10+  | 操作系统                  |
| `JDK`     | 17+  | https://www.injdk.cn/     |
| `NodeJS`  | xxx  | https://nodejs.org/zh-cn/ |
| `NPM`     | xxx  | https://www.npmjs.com/    |

### 服务器环境

| 依赖环境    | 版本                                                         | 备注                                                         |
| ----------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `Anolis OS` | `8.6GA`                                                      | https://openanolis.cn/anolisos                               |
| `Docker`    | latest                                                       | https://www.docker.com/                                      |
| `MySQL`     | 8.0.20                                                       | https://www.mysql.com/cn/                                    |
| `Redis`     | 6.2.7                                                        | https://redis.io/                                            |
| `Nacos`     | 2.1.0                                                        | https://nacos.io/zh-cn/docs/quick-start-docker.html          |
| `Sentinel`  | 1.8.4                                                        | https://github.com/alibaba/Sentinel/releases                 |
| `Seata`     | 1.5.1                                                        | https://github.com/seata/seata                               |
| `RocketMQ`  | 4.9.3                                                        | https://rocketmq.apache.org/                                 |
| `Nginx`     | latest                                                       | https://nginx.org/en/                                        |
| `FastDFS`   | [V6.07](https://github.com/happyfish100/fastdfs/releases/tag/V6.07) | https://gitee.com/fastdfs100                                 |
| `ELK`       | 7.6.2                                                        | https://www.elastic.co/guide/en/elastic-stack/7.6/index.html |
| `MongoDB`   | 4.4.17                                                       | https://www.mongodb.com/try/download/community               |
| `Jenkins`   | latest                                                       | https://www.jenkins.io/zh/doc/book/installing/               |

## 部分功能预览图



## 特别鸣谢

`name`的诞生离不开开源软件和社区的支持，感谢以下开源项目及项目维护者：

- `spring`：https://github.com/spring-projects
- `alibaba`：https://github.com/alibaba
- `mybatis`：https://github.com/mybatis/mybatis-3.git
- `mp`：https://github.com/baomidou
- `api`：https://gitee.com/xiaoym/knife4j
- `vue`：https://github.com/vuejs
- `ui`：https://github.com/ElemeFE
- `oatpp`：https://github.com/oatpp/oatpp

同时也感谢其他没有明确写出来的开源组件提供给与维护者。
