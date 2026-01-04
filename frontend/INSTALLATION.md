# 前端OSS直传功能安装指南

## 快速开始

### 1. 安装依赖

在前端项目目录下运行：

```bash
cd frontend/music-player
npm install
```

这会自动安装新增的 `ali-oss@^6.20.0` 依赖包。

### 2. 开发环境运行

```bash
npm run dev
```

### 3. 生产环境构建

```bash
npm run build
```

构建产物会生成在 `dist` 目录下。

## 依赖说明

### 新增依赖

- **ali-oss** (^6.20.0): 阿里云OSS JavaScript SDK
  - 用途：实现前端直接上传文件到OSS
  - 文档：https://github.com/ali-sdk/ali-oss

### 现有依赖

- **vue** (^3.5.24): Vue.js框架
- **vue-router** (^4.6.4): Vue路由
- **three** (^0.182.0): 3D图形库

## 文件结构

```
frontend/music-player/
├── src/
│   ├── api.js                    # API服务（已更新）
│   ├── utils/
│   │   └── ossUpload.js          # OSS上传工具（新增）
│   └── views/
│       └── Player.vue            # 播放器组件（已更新）
├── package.json                  # 依赖配置（已更新）
├── OSS_UPLOAD_GUIDE.md          # OSS上传使用指南
└── INSTALLATION.md              # 本文件
```

## 功能验证

### 1. 上传封面测试

1. 登录系统
2. 选择一首歌曲
3. 点击"上传封面"按钮
4. 选择图片文件（JPG、PNG等）
5. 点击"确认上传"
6. 观察上传进度条
7. 验证封面是否更新成功

### 2. 上传音频测试

1. 登录系统
2. 选择一首没有音频的歌曲
3. 点击"上传音频"按钮
4. 选择音频文件（MP3、WAV等）
5. 点击"确认上传"
6. 观察上传进度条
7. 验证音频是否可以播放

## 常见问题

### Q1: npm install 失败

**解决方案**:
```bash
# 清除缓存
npm cache clean --force

# 删除 node_modules 和 package-lock.json
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

### Q2: 上传失败，提示"获取上传凭证失败"

**可能原因**:
1. 后端服务未启动
2. 后端接口未部署
3. 网络连接问题

**解决方案**:
- 检查后端服务是否正常运行
- 检查浏览器控制台的网络请求
- 确认后端接口路径正确

### Q3: 上传进度一直显示0%

**可能原因**:
1. OSS配置错误
2. STS凭证无效
3. 网络问题

**解决方案**:
- 检查浏览器控制台错误信息
- 确认OSS Bucket配置正确
- 检查CORS配置

### Q4: 上传成功但文件无法访问

**可能原因**:
1. OSS Bucket权限设置问题
2. 文件URL不正确

**解决方案**:
- 检查OSS Bucket的读权限设置
- 确认返回的URL格式正确
- 检查OSS防盗链设置

## 浏览器兼容性

### 支持的浏览器

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

### 不支持的浏览器

- IE 11及以下（不支持ES6+语法）
- 旧版移动浏览器

## 性能优化建议

### 1. 文件大小限制

建议在前端添加文件大小检查：

```javascript
// 封面图片建议不超过5MB
if (file.size > 5 * 1024 * 1024) {
  alert('封面图片不能超过5MB')
  return
}

// 音频文件建议不超过100MB
if (file.size > 100 * 1024 * 1024) {
  alert('音频文件不能超过100MB')
  return
}
```

### 2. 图片压缩

对于大尺寸图片，可以在上传前进行压缩：

```javascript
// 使用 Canvas API 压缩图片
function compressImage(file, maxWidth = 1920) {
  // 实现图片压缩逻辑
}
```

### 3. 并发控制

如果需要批量上传，建议控制并发数量：

```javascript
// 限制同时上传的文件数量
const MAX_CONCURRENT_UPLOADS = 3
```

## 安全注意事项

1. **STS凭证保护**: 不要在客户端长期存储STS凭证
2. **文件类型验证**: 前后端都要验证文件类型
3. **文件大小限制**: 防止恶意上传超大文件
4. **HTTPS**: 生产环境必须使用HTTPS
5. **CORS配置**: 只允许信任的域名访问

## 技术支持

如有问题，请查看：

1. [OSS上传使用指南](./OSS_UPLOAD_GUIDE.md)
2. [变更总结文档](../../CHANGES_SUMMARY.md)
3. [接口文档](../../documents/接口文档-前后端对接使用.md)

或联系开发团队。
