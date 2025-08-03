import { get, post, request } from './axiosInstance'

/**
 * 获取可监控的服务器列表
 */
export const getServerListApi = (params) => get(`api/metrics/servers`, params)

/**
 * 获取指定服务器的指标数据
 * @param {string} serverId
 */
export const getMetricsDataApi = (serverId) => {
  return request.get('api/metrics/data', {
    params: {
      serverId: serverId,
    },
  })
}
