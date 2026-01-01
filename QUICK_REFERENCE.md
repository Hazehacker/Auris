# OSS直传功能快速参考

## 一、核心概念

### 什么是OSS直传？
前端直接上传文件到阿里云OSS，不经过后端服务器中转。

### 为什么使用OSS直传？
- ✅ 更快的上传速度
- ✅ 降低服务器负载
- ✅ 实时显示上传进度
- ✅ 更高的安全性（临时凭证）

## 二、快速开始

### 前端安装
```bash
cd frontend/music-player
npm install
npm run dev
```

### 后端启动
```bash
cd backend
mvn clean package
java -jar auris-server/target/auris-server.jar
```

## 三、核心API

### 后端接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 接口19 | GET | `/user/track/{id}/cover/v2` | 获取封面上传凭证 |
| 接口20 | GET | `/user/track/{id}/audio/v2` | 获取音频上传凭证 |

### 前端API

```javascript
// 获取封面上传凭证
api.getCoverUploadCredentials(trackId)

// 获取音频上传凭证
api.getAudioUploadCredentials(trackId)

// 上传封面到OSS
uploadCoverToOSS(trackId, file, getCredentials, onProgress)

// 上传音频到OSS
uploadAudioToOSS(trackId, file, getCredentials, onProgress)
```

## 四、使用示例

### 上传封面示例

```javascript
// 1. 获取临时凭证
const response = await api.getCoverUploadCredentials(trackId)
const credentials = response.data

// 2. 上传到OSS
const coverUrl = await uploadCoverToOSS(
  trackId,
  file,
  async (id) => {
    const res = await api.getCoverUploadCredentials(id)
    return res.data
  },
  (progress) => {
    console.log(`上传进度: ${progress}%`)
  }
)

// 3. 更新歌曲封面
song.coverUrl = coverUrl
```

### 上传音频示例

```javascript
// 1. 获取临时凭证
const response = await api.getAudioUploadCredentials(trackId)
const credentials = response.data

// 2. 上传到OSS
const audioUrl = await uploadAudioToOSS(
  trackId,
  file,
  async (id) => {
    const res = await api.getAudioUploadCredentials(id)
    return res.data
  },
  (progress) => {
    console.log(`上传进度: ${progress}%`)
  }
)

// 3. 更新歌曲音频
song.url = audioUrl
```

## 五、文件结构

```
项目根目录/
├── backend/
│   └── auris-server/
│       └── src/main/java/top/hazenix/auris/
│           ├── controller/user/TrackController.java    ✨ 新增v2接口
│           ├── service/ITrackService.java              ✨ 新增验证方法
│           └── service/impl/TrackServiceImpl.java      ✨ 实现验证逻辑
│
├── frontend/music-player/
│   ├── src/
│   │   ├── api.js                                      ✨ 新增API方法
│   │   ├── utils/
│   │   │   └── ossUpload.js                            ✨ 新建工具类
│   │   └── views/
│   │       └── Player.vue                              ✨ 更新上传逻辑
│   ├── package.json                                    ✨ 新增依赖
│   ├── OSS_UPLOAD_GUIDE.md                            📄 使用指南
│   └── INSTALLATION.md                                 📄 安装指南
│
├── documents/
│   └── 接口文档-前后端对接使用.md                      ✨ 更新文档
│
├── CHANGES_SUMMARY.md                                  📄 变更总结
├── DEPLOYMENT_CHECKLIST.md                             📄 部署清单
└── QUICK_REFERENCE.md                                  📄 本文件
```

## 六、常用命令

### 开发环境

```bash
# 前端开发
cd frontend/music-player
npm run dev

# 后端开发
cd backend
mvn spring-boot:run
```

### 生产构建

```bash
# 前端构建
cd frontend/music-player
npm run build

# 后端构建
cd backend
mvn clean package
```

### 依赖管理

```bash
# 安装前端依赖
npm install

# 更新依赖
npm update

# 清除缓存
npm cache clean --force
```

## 七、故障排查

### 问题1：上传失败

**检查项**：
1. 后端服务是否运行？
2. OSS配置是否正确？
3. 网络是否正常？
4. 浏览器控制台有无错误？

**解决方案**：
```bash
# 检查后端日志
tail -f logs/application.log

# 检查网络请求
# 打开浏览器开发者工具 -> Network
```

### 问题2：进度条不显示

**检查项**：
1. 是否导入了 ossUpload.js？
2. 进度回调函数是否正确？
3. CSS样式是否加载？

**解决方案**：
```javascript
// 确认导入
import { uploadCoverToOSS } from '../utils/ossUpload.js'

// 确认进度回调
(progress) => {
  console.log('Progress:', progress)
  uploadCoverProgress.value = progress
}
```

### 问题3：CORS错误

**检查项**：
1. OSS Bucket CORS配置
2. 前端域名是否在白名单

**解决方案**：
在OSS控制台配置CORS规则：
- AllowedOrigin: 前端域名
- AllowedMethod: GET, POST, PUT
- AllowedHeader: *

## 八、性能优化

### 文件大小限制

```javascript
// 封面图片限制
const MAX_COVER_SIZE = 5 * 1024 * 1024 // 5MB

// 音频文件限制
const MAX_AUDIO_SIZE = 100 * 1024 * 1024 // 100MB

if (file.size > MAX_COVER_SIZE) {
  alert('封面图片不能超过5MB')
  return
}
```

### 图片压缩

```javascript
// 使用Canvas压缩图片
function compressImage(file, quality = 0.8) {
  return new Promise((resolve) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      const img = new Image()
      img.onload = () => {
        const canvas = document.createElement('canvas')
        const ctx = canvas.getContext('2d')
        canvas.width = img.width
        canvas.height = img.height
        ctx.drawImage(img, 0, 0)
        canvas.toBlob(resolve, 'image/jpeg', quality)
      }
      img.src = e.target.result
    }
    reader.readAsDataURL(file)
  })
}
```

## 九、安全建议

1. **使用HTTPS** - 生产环境必须使用HTTPS
2. **验证文件类型** - 前后端都要验证
3. **限制文件大小** - 防止恶意上传
4. **临时凭证** - 不要长期存储STS凭证
5. **CORS配置** - 只允许信任的域名

## 十、相关文档

| 文档 | 说明 |
|------|------|
| [OSS_UPLOAD_GUIDE.md](frontend/music-player/OSS_UPLOAD_GUIDE.md) | 详细使用指南 |
| [INSTALLATION.md](frontend/music-player/INSTALLATION.md) | 安装说明 |
| [CHANGES_SUMMARY.md](CHANGES_SUMMARY.md) | 变更总结 |
| [DEPLOYMENT_CHECKLIST.md](DEPLOYMENT_CHECKLIST.md) | 部署清单 |
| [接口文档](documents/接口文档-前后端对接使用.md) | API文档 |

## 十一、技术栈

### 后端
- Java 8+
- Spring Boot
- Aliyun OSS SDK
- STS (Security Token Service)

### 前端
- Vue 3
- ali-oss (JavaScript SDK)
- Vite

## 十二、联系方式

如有问题，请：
1. 查看相关文档
2. 检查浏览器控制台
3. 查看后端日志
4. 联系开发团队

---

**最后更新**: 2025-01-01
**版本**: 1.0.0
