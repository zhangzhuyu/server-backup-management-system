// src/api/server.js
import { get, post, request } from './axiosInstance'
// **** 注意：确认服务器服务的 context-path ****
// 之前是 /api/p3/server，如果现在统一为 /api/ts-dc-backup，请修改
// 假设仍然是 /api/p3
// const SERVER_SERVICE_PATH = '/api/p3/server'; // <<=== ！！！需要确认这个路径！！！

export const queryServers = (params) => get(`server/query`, params)

/**
 * 服务器信息表_添加
 * @param {object} data - ServerDto
 */
export function addServer(data) {
  return request({
    url: `server/create`,
    method: 'post',
    data: data,
  })
}

/**
 * 服务器信息表_删除
 * @param {number[]|string[]} ids - 要删除的服务器 ID 数组 (后端期望 Long[])
 */
export function deleteServer(ids) {
  // 后端接口是 POST /delete，请求体是 ID 数组
  return request({
    url: `server/delete`,
    method: 'post',
    // 确保传递的是数组
    data: Array.isArray(ids) ? ids : [ids],
  })
}

/**
 * 服务器信息表_更新
 * @param {object} data - ServerDto (必须包含 id)
 */
export function updateServer(data) {
  return request({
    url: `server/update`,
    method: 'post',
    data: data,
  })
}

// 新增：测试服务器连接
export const testServerConnection = (serverDto) => {
  // 后端要求 ServerDto 只包含 id
  // 我们直接传递包含 id 的对象作为请求体
  return request.post(`/server/testConnection`, serverDto)
}

// 其他服务器相关 API...
