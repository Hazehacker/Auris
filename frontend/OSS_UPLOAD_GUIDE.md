
# OSS直传功能使用指南

## 概述

前端已升级为使用阿里云OSS直传方式上传文件，不再通过后端服务器中转。这种方式具有以下优势：

1. **提高上传速度** - 文件直接上传到OSS，避免后端服务器带宽限制
2. **降低服务器负载** - 后端服务器不再处理大文件传输
3. **更好的用户体验** - 实时显示上传进度
4. **更高的安全性** - 使用STS临时凭证，凭证有时效性

## 技术实现

### 后端接口

新增了两个v2版本的接口用于获取STS临时凭证：

- `GET /user/track/{id}/cover/v2` - 获取上传封面的临时凭证
- `GET /user/track/{id}/audio/v2` - 获取上传音频的临时凭证

这两个接口会：
1. 验证歌曲ID是否有效
2. 验证歌曲是否存在
3. 返回STS临时凭证（包含accessKeyId、accessKeySecret、securityToken等）

### 前端实现

#### 1. OSS上传工具类 (`src/utils/ossUpload.js`)

提供了三个核心函数：

- `uploadToOSS(file, credentials, onProgress)` - 通用OSS上传函数
- `uploadCoverToOSS(trackId, file, getCredentials, onProgress)` - 上传封面专用
- `uploadAudioToOSS(trackId, file, getCredentials, onProgress)` - 上传音频专用

#### 2. API服务更新 (`src/api.js`)

新增了三个API方法：

```javascript
// 获取通用STS临时凭证
getTempCredentials()

// 获取上传封面的临时凭证
getCoverUploadCredentials(trackId)

// 获取上传音频的临时凭证
getAudioUploadCredentials(trackId)
```

#### 3. 播放器组件更新 (`src/views/Player.vue`)

- 添加了上传进度显示（`uploadCoverProgress`、`uploadAudioProgress`）
- 修改了上传逻辑，使用OSS直传
- 添加了进度条UI组件

## 安装依赖

在前端项目目录下运行：

```bash
npm install
```

这会安装新增的 `ali-oss` 依赖包。

## 使用流程

### 上传封面流程

1. 用户点击"上传封面"按钮
2. 选择图片文件
3. 点击"确认上传"
4. 前端调用 `getCoverUploadCredentials(trackId)` 获取临时凭证
5. 使用临时凭证直接上传文件到OSS
6. 实时显示上传进度
7. 上传完成后更新歌曲封面URL

### 上传音频流程

1. 用户点击"上传音频"按钮
2. 选择音频文件
3. 点击"确认上传"
4. 前端调用 `getAudioUploadCredentials(trackId)` 获取临时凭证
5. 使用临时凭证直接上传文件到OSS
6. 实时显示上传进度
7. 上传完成后更新歌曲音频URL

## 文件命名规则

上传到OSS的文件会自动生成唯一文件名：

```
uploads/{timestamp}_{randomStr}{extension}
```

例如：`uploads/1704067200000_a3b5c7.mp3`

## 错误处理

系统会处理以下错误情况：

1. **文件类型错误** - 封面必须是图片，音频必须是音频文件
2. **歌曲不存在** - 后端会验证歌曲ID
3. **凭证获取失败** - 显示友好的错误提示
4. **上传失败** - 捕获并显示错误信息

## OSS CORS配置（重要！）

**前端直传OSS必须配置CORS规则，否则会出现跨域错误。**

### 配置步骤

1. 登录阿里云OSS控制台
2. 选择对应的Bucket
3. 进入 **权限管理** > **跨域设置（CORS）**
4. 点击 **创建规则**，配置如下：

**来源（AllowedOrigins）：**
```
http://localhost:5173
https://auris.hazenix.top
```
⚠️ **重要提示：**
- ✅ **正确格式**：`https://auris.hazenix.top`（不带路径，不带通配符）
- ❌ **错误格式**：`https://auris.hazenix.top/*`（不能使用路径通配符）
- ❌ **错误格式**：`https://*.hazenix.top`（OSS不支持子域通配符）
- 如果有多个域名，每行一个
- 必须包含协议（http:// 或 https://）
- 如果使用非标准端口，必须包含端口号（如 `http://localhost:5173`）

**允许Methods：**
```
GET, POST, PUT, DELETE, HEAD
```

**允许Headers：**
```
*
```

**暴露Headers：**
```
ETag, x-oss-request-id
```

**MaxAgeSeconds：**
```
3600
```

### 常见错误和解决方案

#### 错误1：跨域请求失败（CORS错误）

**错误表现：**
- `XHR 错误（请求"错误"），PUT http://... -1`
- `已连接：false，保持连接套接字：false`
- 浏览器控制台显示CORS相关错误
- 状态码为 `-1` 的请求失败
- 错误信息包含 "CORS" 或 "Access-Control-Allow-Origin"

**可能原因：**
1. ❌ **来源格式错误** - 最常见的问题！
   - 错误：`https://auris.hazenix.top/*`（使用了路径通配符）
   - 正确：`https://auris.hazenix.top`（不带路径和通配符）
   
2. ❌ **缺少DELETE方法**
   - 分片上传需要DELETE方法来清理失败的分片
   - 必须包含：`PUT, POST, GET, DELETE, HEAD`
   
3. ❌ **来源不匹配**
   - 实际请求的Origin与配置的来源不完全一致
   - 检查浏览器Network标签中的请求头，确认实际的Origin值
   
4. ❌ **配置未生效**
   - CORS配置保存后需要等待1-2分钟才能生效
   - 清除浏览器缓存后重试

**解决方法：**
1. 检查并修正来源格式（去掉路径通配符）
2. 确保包含所有必要的方法（特别是DELETE）
3. 在浏览器开发者工具的Network标签中查看实际请求的Origin
4. 保存配置后等待1-2分钟
5. 清除浏览器缓存并刷新页面

### 验证CORS配置

配置完成后，可以通过以下方式验证：

1. **检查浏览器控制台**
   - 打开浏览器开发者工具（F12）
   - 切换到 Network 标签
   - 尝试上传文件
   - 查看请求的响应头，应该包含 `Access-Control-Allow-Origin`

2. **使用curl测试（可选）**
   ```bash
   curl -X OPTIONS https://your-bucket.oss-region.aliyuncs.com/ \
     -H "Origin: http://localhost:5173" \
     -H "Access-Control-Request-Method: PUT" \
     -v
   ```
   应该返回 `Access-Control-Allow-Origin` 头

3. **检查配置项**
   - ✅ 来源（AllowedOrigins）必须包含你的前端域名（包括协议和端口）
   - ✅ 允许方法必须包含 `PUT`
   - ✅ 允许Headers建议设置为 `*`（允许所有）
   - ✅ 配置保存后可能需要等待几分钟生效

### 故障排查清单

如果上传仍然失败，请逐项检查：

**CORS配置检查：**
- [ ] OSS Bucket的CORS规则已正确配置
- [ ] 来源格式正确（不带路径通配符，如 `https://auris.hazenix.top` 而不是 `https://auris.hazenix.top/*`）
- [ ] 前端域名（包括协议和端口）已添加到AllowedOrigins
- [ ] 允许方法包含 `PUT, POST, GET, DELETE, HEAD`（DELETE很重要！）
- [ ] 允许Headers设置为 `*`
- [ ] 暴露Headers包含 `ETag, x-oss-request-id`
- [ ] MaxAgeSeconds设置为 `3600`
- [ ] CORS配置已保存并生效（等待1-2分钟）

**浏览器检查：**
- [ ] 打开浏览器开发者工具（F12）> Network标签
- [ ] 尝试上传文件，查看失败的请求
- [ ] 检查请求的Origin头是否与CORS配置的来源完全匹配
- [ ] 检查响应头是否包含 `Access-Control-Allow-Origin`
- [ ] 清除浏览器缓存后重试

**其他检查：**
- [ ] 浏览器控制台没有其他错误
- [ ] STS临时凭证有效（未过期）
- [ ] 网络连接正常
- [ ] OSS的endpoint配置正确

## 注意事项

1. **STS凭证有效期** - 临时凭证有时效性，通常为1小时
2. **文件大小限制** - 建议音频文件不超过100MB，封面图片不超过5MB
3. **网络超时** - OSS客户端设置了10分钟超时时间
4. **浏览器兼容性** - 需要支持File API和Fetch API的现代浏览器
5. **CORS配置** - ⚠️ **必须**在OSS控制台配置CORS规则，否则无法上传

## 后续优化建议

1. 添加文件大小限制提示
2. 支持断点续传
3. 添加上传队列管理
4. 支持批量上传
5. 添加上传速度显示
