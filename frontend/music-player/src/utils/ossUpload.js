// OSS直传工具类
import OSS from 'ali-oss'

/**
 * 使用STS临时凭证上传文件到阿里云OSS
 * @param {File} file - 要上传的文件
 * @param {Object} credentials - STS临时凭证
 * @param {Function} onProgress - 上传进度回调函数
 * @returns {Promise<string>} 返回文件的完整URL
 */
export async function uploadToOSS(file, credentials, onProgress) {
  if (!file) {
    throw new Error('文件不能为空')
  }

  if (!credentials || !credentials.accessKeyId || !credentials.accessKeySecret || !credentials.securityToken) {
    throw new Error('STS凭证不完整')
  }

  try {
    // 创建OSS客户端配置
    const ossConfig = {
      region: credentials.region || 'oss-cn-hangzhou',
      accessKeyId: credentials.accessKeyId,
      accessKeySecret: credentials.accessKeySecret,
      stsToken: credentials.securityToken,
      bucket: credentials.bucket,
      // 设置超时时间
      timeout: 600000, // 10分钟
      // 浏览器环境下的特殊配置
      secure: true, // 使用HTTPS
      // 不使用内网endpoint（浏览器必须使用公网）
      internal: false,
      // 如果提供了endpoint，使用自定义endpoint
      endpoint: credentials.endpoint || undefined,
      // 不使用CNAME
      cname: false
    }

    // 移除undefined的配置项
    Object.keys(ossConfig).forEach(key => {
      if (ossConfig[key] === undefined) {
        delete ossConfig[key]
      }
    })

    console.log('OSS配置:', { ...ossConfig, accessKeySecret: '***', stsToken: '***' })

    // 创建OSS客户端
    const client = new OSS(ossConfig)

    // 生成唯一的文件名
    const timestamp = Date.now()
    const randomStr = Math.random().toString(36).substring(2, 8)
    const ext = file.name.substring(file.name.lastIndexOf('.'))
    const fileName = `uploads/${timestamp}_${randomStr}${ext}`

    console.log('开始上传文件:', {
      fileName,
      fileSize: file.size,
      fileType: file.type,
      bucket: credentials.bucket
    })

    // 对于大文件（>100MB），使用分片上传
    const useMultipart = file.size > 100 * 1024 * 1024 // 100MB

    let result
    if (useMultipart) {
      console.log('使用分片上传（文件较大）')
      // 分片上传
      result = await client.multipartUpload(fileName, file, {
        progress: (p, cpt, res) => {
          if (onProgress && typeof onProgress === 'function') {
            onProgress(Math.floor(p * 100))
          }
        },
        partSize: 1024 * 1024 * 5 // 5MB per part
      })
    } else {
      // 普通上传
      result = await client.put(fileName, file, {
        progress: (p) => {
          if (onProgress && typeof onProgress === 'function') {
            onProgress(Math.floor(p * 100))
          }
        },
        headers: {
          'Content-Type': file.type || 'application/octet-stream'
        }
      })
    }

    console.log('上传成功:', result.url)

    // 返回文件URL
    return result.url
  } catch (error) {
    console.error('OSS上传失败:', error)
    console.error('错误详情:', {
      name: error.name,
      message: error.message,
      code: error.code,
      requestId: error.requestId,
      hostId: error.hostId,
      status: error.status,
      requestUrls: error.requestUrls
    })
    
    // 根据错误类型提供更友好的错误信息
    let errorMessage = 'OSS上传失败'
    
    // 检查是否是网络错误或CORS错误
    const errorStr = JSON.stringify(error).toLowerCase()
    const messageStr = (error.message || '').toLowerCase()
    
    if (error.code === 'AccessDenied') {
      errorMessage = '访问被拒绝，请检查凭证是否有效'
    } else if (error.code === 'InvalidAccessKeyId') {
      errorMessage = '访问密钥无效，请重新获取凭证'
    } else if (error.code === 'SignatureDoesNotMatch') {
      errorMessage = '签名不匹配，请重新获取凭证'
    } else if (error.status === -1 || messageStr.includes('cors') || messageStr.includes('network') || messageStr.includes('failed')) {
      errorMessage = '❌ 跨域请求失败！\n\n这通常是因为OSS的CORS配置不正确。\n\n请按以下步骤配置：\n1. 登录阿里云OSS控制台\n2. 选择Bucket: ' + (credentials.bucket || '你的Bucket') + '\n3. 进入"权限管理" > "跨域设置（CORS）"\n4. 添加规则，允许来源：' + window.location.origin + '\n5. 允许方法：PUT, POST, GET, DELETE, HEAD\n6. 允许Headers：*\n7. 暴露Headers：ETag, x-oss-request-id'
    } else if (error.code === 'RequestTimeout') {
      errorMessage = '请求超时，请检查网络连接或稍后重试'
    } else if (error.message) {
      errorMessage = error.message
    }
    
    throw new Error(errorMessage)
  }
}

/**
 * 上传封面图片到OSS
 * @param {number} trackId - 歌曲ID
 * @param {File} file - 封面图片文件
 * @param {Function} getCredentials - 获取临时凭证的函数
 * @param {Function} onProgress - 上传进度回调
 * @returns {Promise<string>} 返回封面URL
 */
export async function uploadCoverToOSS(trackId, file, getCredentials, onProgress) {
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    throw new Error('请选择图片文件')
  }

  // 获取临时凭证
  const credentials = await getCredentials(trackId)
  
  // 上传到OSS
  return await uploadToOSS(file, credentials, onProgress)
}

/**
 * 上传音频文件到OSS
 * @param {number} trackId - 歌曲ID
 * @param {File} file - 音频文件
 * @param {Function} getCredentials - 获取临时凭证的函数
 * @param {Function} onProgress - 上传进度回调
 * @returns {Promise<string>} 返回音频URL
 */
export async function uploadAudioToOSS(trackId, file, getCredentials, onProgress) {
  // 验证文件类型
  const audioExtensions = ['.mp3', '.wav', '.flac', '.aac', '.ogg', '.m4a']
  const fileName = file.name.toLowerCase()
  const isAudio = file.type.startsWith('audio/') || audioExtensions.some(ext => fileName.endsWith(ext))
  
  if (!isAudio) {
    throw new Error('请选择音频文件')
  }

  // 获取临时凭证
  const credentials = await getCredentials(trackId)
  
  // 上传到OSS
  return await uploadToOSS(file, credentials, onProgress)
}
