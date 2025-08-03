import { request } from './axiosInstance' // 确保你导入了正确的 axios 实例

// 获取所有系统设置
export const getSystemSettingsApi = () => {
  return request.get('/api/system-settings')
}

// 保存所有系统设置
export const saveSystemSettingsApi = (data) => {
  // data 是一个包含所有设置的 key-value 对象
  return request.post('/api/system-settings', data)
}
