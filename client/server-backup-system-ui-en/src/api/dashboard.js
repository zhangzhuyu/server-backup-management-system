// src/api/dashboard.js
import { request } from './axiosInstance' // 确认你的 axios 实例路径

/**
 * 获取监控大屏的统计数据
 */
export const getDashboardStats = () => {
  return request({
    url: '/dashboard/stats',
    method: 'get',
  })
}
