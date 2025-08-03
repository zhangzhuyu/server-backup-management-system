// src/api/database.js
import { get, post, request } from './axiosInstance'
// **** 注意：确认数据库服务的 context-path ****
// 假设也通过 /api 代理，但路径可能不同
// const DATABASE_SERVICE_PATH = '/api/ts-dc-database'; // <<=== ！！！需要确认这个路径！！！

// 获取数据库列表 (修改为 POST，接收查询参数和请求体)
export const getDatabaseList = (queryParams, dataBody) => {
  return request({
    url: `database/list`,
    method: 'post', // **** 必须是 POST ****
    params: queryParams, // **** 查询参数放在 params 里 ****
    data: dataBody, // **** 请求体放在 data 里 ****
  })
}

/**
 * 添加数据库信息
 * @param {object} data - DatabaseDto
 */
export function addDatabase(data) {
  return request({
    url: `database/insert`,
    method: 'post',
    data: data,
  })
}

/**
 * 根据 ID 测试数据库连接
 * @param {string} id - 数据库主键 ID
 */
export function testDatabaseConnection(params) {
  // 确保 params 是一个对象并且包含 id 属性
  if (!params || typeof params !== 'object' || params.id === undefined || params.id === null) {
    return Promise.reject(
      new Error('testDatabase function requires an object with an id property.'),
    )
  }

  const databaseId = params.id // 从对象中提取 ID

  return request({
    // 使用模板字符串正确地将 ID 拼接到 URL 路径中
    url: `/database/test/${databaseId}`, // <--- 修改这里！
    method: 'get', // 或者 'post', 'put' 等，根据你的后端接口定义
    // 如果是 GET 请求，通常不需要 data 或 params (除非有其他查询参数)
    // 如果是 POST 请求，可能需要在 data 中发送些什么，但 URL 路径通常是这样
  })
}

/**
 * 批量删除数据库信息
 * @param {string[]} ids - 要删除的数据库 ID 数组
 */
export function deleteDatabase(ids) {
  // 后端接口是 POST /deleteByMulti，请求体是 { "ids": [...] }
  return request({
    url: `database/deleteByMulti`,
    method: 'post',
    data: { ids: ids },
  })
}

/**
 * 更新数据库信息
 * @param {object} data - DatabaseDto (必须包含 id)
 */
export function updateDatabase(data) {
  return request({
    url: `database/update`,
    method: 'post',
    data: data,
  })
}

// 其他数据库相关 API...
