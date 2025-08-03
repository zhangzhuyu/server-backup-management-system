// src/api/axiosInstance.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router' // 引入 router 用于处理 401 等情况
// import { useAuthStore } from '@/stores/auth'; // 如果需要获取 token

// 从环境变量或配置文件读取基础 URL
// 在项目根目录创建 .env.development 和 .env.production 文件
// .env.development:
// VITE_API_BASE_URL=/api/p3  <-- 用于开发代理
// .env.production:
// VITE_API_BASE_URL=http://your-production-api-server.com/api/p3 <-- 部署地址

// 在 vite.config.js 中配置代理 (用于开发环境跨域)
// import { defineConfig } from 'vite'
// import vue from '@vitejs/plugin-vue'
// export default defineConfig({
//   plugins: [vue()],
//   server: {
//     proxy: {
//       '/api/p3': { // 匹配 VITE_API_BASE_URL
//         target: 'http://your-backend-dev-server.com', // 你的后端开发服务器地址
//         changeOrigin: true,
//         // rewrite: (path) => path.replace(/^\/api\/p3/, '') // 如果后端接口不带 /api/p3 前缀，需要重写
//       }
//     }
//   }
// })

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api' // 设置基础 URL

const instance = axios.create({
  baseURL: baseURL,
  timeout: 30000, // 增加超时时间
})

// 请求拦截器
instance.interceptors.request.use(
  (config) => {
    // 可在此处添加认证头，例如从 Pinia store 获取 token
    // const authStore = useAuthStore();
    // if (authStore.token) {
    //   config.headers['Authorization'] = `Bearer ${authStore.token}`;
    // }
    return config
  },
  (error) => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  },
)

// 响应拦截器
instance.interceptors.response.use(
  (response) => {
    // 检查是否是文件下载响应
    const contentType = response.headers['content-type']
    const disposition = response.headers['content-disposition']
    if (
      response.data instanceof Blob &&
      (contentType?.includes('application/octet-stream') ||
        contentType?.includes('application/x-download') ||
        disposition?.includes('attachment'))
    ) {
      // 返回整个响应对象，以便获取文件名和 Blob 数据
      return response
    }

    // 处理普通 JSON 响应 (根据你的 WebResponse 结构)
    const res = response.data

    // 假设后端成功时 success 为 true，失败时为 false，数据在 data 字段，消息在 message 字段
    if (res && typeof res.success === 'boolean') {
      if (res.success) {
        // 后端明确表示成功
        return res.data // 只返回业务数据
      } else {
        // 后端明确表示失败
        const message = res.message || '操作失败'
        ElMessage.error(message)
        return Promise.reject(new Error(message)) // 返回拒绝的 Promise
      }
    } else {
      // 如果后端响应结构不符合预期，可以作为错误处理或直接返回
      // console.warn('Unrecognized API response structure:', res);
      // 也可以尝试直接返回 data，如果后端有时不包装 WebResponse
      return response.data
    }
  },
  (error) => {
    console.error('Response Error:', error)
    let message = '网络错误，请稍后重试'
    if (error.response) {
      // 服务器返回了错误状态码
      switch (error.response.status) {
        case 401:
          message = '未授权或登录超时，请重新登录'
          // 清除本地登录状态并跳转到登录页
          // const authStore = useAuthStore();
          // authStore.logout(); // 假设 store 里有 logout 方法处理跳转
          router.push('/login') // 直接跳转
          break
        case 403:
          message = '您没有权限执行此操作'
          break
        case 404:
          message = '请求的资源未找到'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          // 尝试从后端返回的错误信息中获取更具体的消息
          if (error.response.data && error.response.data.message) {
            message = error.response.data.message
          } else {
            message = `请求错误 (${error.response.status})`
          }
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      message = '无法连接到服务器，请检查网络'
    } else {
      // 设置请求时触发了一个错误
      message = error.message || '请求发起失败'
    }
    ElMessage.error(message)
    return Promise.reject(error) // 将错误继续传递下去
  },
)

// 封装 get, post, download 方法
const get = (url, params) => instance.get(url, { params })
const post = (url, data) => instance.post(url, data)

export const request = instance
// 专门的下载方法
const download = async (url, params, method = 'GET', filenamePrefix = 'download') => {
  try {
    const response = await instance({
      url: url,
      method: method,
      params: method === 'GET' ? params : null,
      data: method === 'POST' ? params : null,
      responseType: 'blob', // 关键：设置响应类型为 blob
    })

    const blob = response.data
    if (!blob || blob.size === 0) {
      ElMessage.warning('下载的文件内容为空')
      return
    }

    // 从响应头获取文件名
    let filename = `${filenamePrefix}_${Date.now()}.bin` // 默认文件名
    const disposition = response.headers['content-disposition']
    if (disposition) {
      const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
      const matches = filenameRegex.exec(disposition)
      if (matches != null && matches[1]) {
        filename = decodeURIComponent(matches[1].replace(/['"]/g, ''))
      } else {
        // 尝试另一种常见的格式 filename*=UTF-8''xxxxx
        const utf8FilenameRegex = /filename\*=UTF-8''([\w%\.-]+)/i
        const utf8Matches = utf8FilenameRegex.exec(disposition)
        if (utf8Matches != null && utf8Matches[1]) {
          try {
            filename = decodeURIComponent(utf8Matches[1])
          } catch (e) {
            console.error('Error decoding filename*:', e)
            // fallback to the simple filename if decoding fails
            const simpleFilenameRegex = /filename="?([^"]+)"?/
            const simpleMatches = simpleFilenameRegex.exec(disposition)
            if (simpleMatches != null && simpleMatches[1]) {
              filename = simpleMatches[1]
            }
          }
        }
      }
    }

    // 创建下载链接
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.setAttribute('download', filename) // 设置下载文件名
    document.body.appendChild(link)
    link.click() // 触发下载

    // 清理
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)
    ElMessage.success('文件下载已开始')
  } catch (error) {
    console.error('Download Error in helper:', error)
    // 错误已在响应拦截器中处理并提示，这里不再重复提示
    // 可以选择性地向上抛出错误或返回特定值
    // throw error; // 如果上层需要知道下载失败
  }
}

export { get, post, download, instance as axiosInstance } // 导出实例以便特殊情况使用
