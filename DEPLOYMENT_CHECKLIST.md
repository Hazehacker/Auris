# OSS直传功能部署检查清单

## 部署前检查

### 后端检查

- [ ] **代码更新确认**
  - [ ] TrackController.java 已添加 v2 接口
  - [ ] ITrackService.java 已添加验证方法
  - [ ] TrackServiceImpl.java 已实现验证逻辑
  - [ ] 所有代码无编译错误

- [ ] **OSS配置检查**
  - [ ] application.yml 中 OSS 配置正确
  - [ ] AccessKey 和 SecretKey 已配置
  - [ ] Bucket 名称正确
  - [ ] Region 设置正确
  - [ ] STS角色配置正确

- [ ] **权限配置**
  - [ ] STS角色有上传权限
  - [ ] STS角色有读取权限
  - [ ] 权限策略配置正确

### 前端检查

- [ ] **代码更新确认**
  - [ ] api.js 已添加新接口方法
  - [ ] ossUpload.js 工具类已创建
  - [ ] Player.vue 已更新上传逻辑
  - [ ] 进度条样式已添加

- [ ] **依赖安装**
  - [ ] package.json 已添加 ali-oss 依赖
  - [ ] 运行 `npm install` 成功
  - [ ] node_modules 中有 ali-oss 包

- [ ] **构建测试**
  - [ ] `npm run dev` 开发环境运行正常
  - [ ] `npm run build` 生产构建成功
  - [ ] 无 ESLint 错误
  - [ ] 无 TypeScript 错误（如果使用）

### OSS配置检查

- [ ] **Bucket配置**
  - [ ] Bucket 已创建
  - [ ] Bucket 权限设置为公共读或私有
  - [ ] 存储类型选择合适（标准、低频等）

- [ ] **CORS配置**
  - [ ] 允许的来源（Origin）包含前端域名
  - [ ] 允许的方法包含 PUT、POST、GET
  - [ ] 允许的头部包含必要字段
  - [ ] 示例配置：
    ```xml
    <CORSRule>
      <AllowedOrigin>https://your-frontend-domain.com</AllowedOrigin>
      <AllowedMethod>GET</AllowedMethod>
      <AllowedMethod>POST</AllowedMethod>
      <AllowedMethod>PUT</AllowedMethod>
      <AllowedHeader>*</AllowedHeader>
      <ExposeHeader>ETag</ExposeHeader>
      <MaxAgeSeconds>3600</MaxAgeSeconds>
    </CORSRule>
    ```

- [ ] **防盗链配置**（可选）
  - [ ] Referer 白名单配置
  - [ ] 空 Referer 处理策略

## 部署步骤

### 1. 后端部署

```bash
# 1. 进入后端项目目录
cd backend

# 2. 清理并编译
mvn clean package

# 3. 停止旧服务
# 根据实际情况执行

# 4. 启动新服务
java -jar auris-server/target/auris-server.jar

# 5. 检查服务状态
curl http://localhost:9090/health
```

- [ ] 后端服务启动成功
- [ ] 健康检查通过
- [ ] 日志无错误信息

### 2. 前端部署

```bash
# 1. 进入前端项目目录
cd frontend/music-player

# 2. 安装依赖
npm install

# 3. 构建生产版本
npm run build

# 4. 部署 dist 目录到服务器
# 根据实际情况执行（nginx、CDN等）
```

- [ ] 构建成功
- [ ] dist 目录生成
- [ ] 静态资源部署完成

### 3. Nginx配置（如果使用）

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态资源
    location / {
        root /path/to/dist;
        try_files $uri $uri/ /index.html;
    }
    
    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:9090/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

- [ ] Nginx配置已更新
- [ ] Nginx配置测试通过 (`nginx -t`)
- [ ] Nginx已重载 (`nginx -s reload`)

## 部署后测试

### 功能测试

- [ ] **用户登录**
  - [ ] 登录功能正常
  - [ ] Token 获取成功

- [ ] **封面上传**
  - [ ] 点击上传封面按钮正常
  - [ ] 文件选择对话框打开
  - [ ] 选择图片后预览显示
  - [ ] 点击确认上传
  - [ ] 进度条显示正常
  - [ ] 上传成功后封面更新
  - [ ] 刷新页面封面仍然显示

- [ ] **音频上传**
  - [ ] 点击上传音频按钮正常
  - [ ] 文件选择对话框打开
  - [ ] 选择音频后文件信息显示
  - [ ] 点击确认上传
  - [ ] 进度条显示正常
  - [ ] 上传成功后音频可播放
  - [ ] 刷新页面音频仍可播放

### 错误处理测试

- [ ] **无效文件类型**
  - [ ] 上传非图片文件到封面，显示错误提示
  - [ ] 上传非音频文件到音频，显示错误提示

- [ ] **网络错误**
  - [ ] 断网情况下上传，显示错误提示
  - [ ] 网络恢复后可重新上传

- [ ] **权限错误**
  - [ ] 未登录时上传，提示登录
  - [ ] Token过期时上传，提示重新登录

- [ ] **歌曲不存在**
  - [ ] 上传不存在的歌曲ID，显示错误提示

### 性能测试

- [ ] **小文件上传**（< 1MB）
  - [ ] 上传速度快
  - [ ] 进度条流畅

- [ ] **中等文件上传**（1-10MB）
  - [ ] 上传速度正常
  - [ ] 进度条更新及时

- [ ] **大文件上传**（> 10MB）
  - [ ] 上传不超时
  - [ ] 进度条准确显示

### 兼容性测试

- [ ] **Chrome浏览器**
  - [ ] 上传功能正常
  - [ ] UI显示正常

- [ ] **Firefox浏览器**
  - [ ] 上传功能正常
  - [ ] UI显示正常

- [ ] **Safari浏览器**
  - [ ] 上传功能正常
  - [ ] UI显示正常

- [ ] **移动端浏览器**
  - [ ] 上传功能正常
  - [ ] UI适配良好

## 监控和日志

### 后端日志检查

- [ ] 查看应用日志
  ```bash
  tail -f logs/application.log
  ```

- [ ] 检查关键日志
  - [ ] "获取上传封面的临时凭证" 日志
  - [ ] "获取上传音频的临时凭证" 日志
  - [ ] 无异常堆栈信息

### 前端日志检查

- [ ] 打开浏览器开发者工具
- [ ] 检查 Console 无错误
- [ ] 检查 Network 请求正常
  - [ ] `/user/track/{id}/cover/v2` 返回 200
  - [ ] `/user/track/{id}/audio/v2` 返回 200
  - [ ] OSS上传请求成功

### OSS监控

- [ ] 登录阿里云OSS控制台
- [ ] 检查上传统计
  - [ ] 上传请求数正常
  - [ ] 上传成功率高
  - [ ] 无异常流量

## 回滚计划

如果部署出现问题，按以下步骤回滚：

### 后端回滚

```bash
# 1. 停止新服务
# 2. 启动旧版本服务
# 3. 验证服务正常
```

- [ ] 回滚脚本准备完成
- [ ] 旧版本备份存在

### 前端回滚

```bash
# 1. 恢复旧版本 dist 目录
# 2. 重新部署
# 3. 清除浏览器缓存
```

- [ ] 旧版本备份存在
- [ ] 回滚步骤明确

## 文档更新

- [ ] API文档已更新
- [ ] 用户手册已更新（如有）
- [ ] 开发文档已更新
- [ ] 变更日志已记录

## 团队通知

- [ ] 通知测试团队进行测试
- [ ] 通知运维团队部署完成
- [ ] 通知产品团队功能上线
- [ ] 更新项目管理系统状态

## 后续优化

- [ ] 收集用户反馈
- [ ] 监控上传成功率
- [ ] 分析上传性能数据
- [ ] 规划下一步优化方向

## 签字确认

- [ ] 开发负责人：__________ 日期：__________
- [ ] 测试负责人：__________ 日期：__________
- [ ] 运维负责人：__________ 日期：__________
- [ ] 产品负责人：__________ 日期：__________

---

**备注**：
- 所有检查项必须完成才能进行生产部署
- 如有任何问题，立即停止部署并排查
- 保持与团队的及时沟通
