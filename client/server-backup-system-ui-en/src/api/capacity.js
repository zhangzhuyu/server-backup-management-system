// src/api/capacity.js
import { request } from './axiosInstance'

/**
 * 根据服务器ID获取预测数据
 * @param {string} serverId
 */
export const getPredictionDataForServerApi = (serverId) => {
  return request.get(`api/capacity/prediction/server/${serverId}`)
}

// 复用指标监控里的服务器列表接口
export const getServerListForCapacityApi = () => {
  return request.get('api/metrics/servers')
}
