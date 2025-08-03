// src/api/backup.js
import { get, post, request } from './axiosInstance'

/**
 * 获取备份策略列表
 * @param {object} params - 查询参数 { page, pageSize, title, dataSourceType?, backupWay?, totalMethod? }
 * @returns {Promise<IPage<LyDbBackupStrategyRecordVo>>} - 后端直接返回 IPage 对象
 */
export const getStrategyList = (params) => {
  // 后端期望 dataSourceType, backupWay, totalMethod 是 List<String>
  // 如果前端传入的是单个值或逗号分隔字符串，需要转换
  // Element Plus Table 的 filter 传递的是数组，正好匹配
  return get('/backup/list', params)
}

/**
 * 添加备份策略
 * @param {LyDbBackupStrategyRecordDto} data - 策略数据
 */
export const addStrategy = (data) => post('/backup/insert', data)

/**
 * 更新备份策略
 * @param {LyDbBackupStrategyRecordDto} data - 策略数据 (必须包含 id)
 */
export const updateStrategy = (data) => post('/backup/update', data)

/**
 * 批量删除备份策略
 * @param {Array<number|string>} ids - 要删除的策略 ID 列表
 */
export const deleteStrategies = (ids) => post('/backup/delete', ids)

/**
 * 立即启动备份
 * @param {number|string} id - 策略 ID
 */
export const startBackupNow = (id) => post('/backup/backup', { id }) // 后端期望 { "id": xxx }

/**
 * 启用/禁用备份策略 (根据后端 API /backup/startBackup 的实际作用调整)
 * 假设 /startBackup 是用来切换 enable 状态的 (启用/禁用)
 * @param {number|string} id - 策略 ID
 */
export const toggleStrategyEnable = (id) => post('/backup/startBackup', { id })

/**
 * 停止正在进行的备份 (需要历史记录 ID)
 * @param {Array<number|string>} historyIds - 要停止的备份历史记录 ID 列表
 */
export const stopBackupProgress = (historyIds) => post('/backup/stopBackup', historyIds)

export const restartBackup = (historyIds) => post('/backup/restartBackup', [historyIds])

// **** 新增/修改：获取备份目标服务器列表 ****
export const getBackupTargetListApi = (params) => {
  // params: { sourceType?, backupWay, backupMethod? }
  return request.get(`/backup/getBackupTargetList`, { params })
}

// **** 新增：获取数据库列表 ****
export const getDatabasesApi = (params) => {
  // params: { sourceType, id } (id 是目标服务器 ID)
  return request.get(`/backup/selectDatabases`, { params })
}

// **** 新增：获取表格列表 ****
// 函数接收数据库 ID 作为参数
export const getTablesForDatabaseApi = (dbId) => {
  const queryParams = {
    url: dbId,
  }
  // 只传递 params 对象，而不是包含 params 的 config 对象
  return get(`/backup/selectTables`, queryParams)
}

// **** 新增：获取源服务器列表 (用于服务器备份) ****
// 注意：这使用了 serverService 的 API，路径可能不同
// 需要确认你的项目结构和 Vite 代理配置是否能正确访问
export const getServerListApi = (params) => {
  // params: { page, pageSize, content?, affiliatedCompany? }
  // 假设服务器管理的 context-path 也是 /api/ts-dc-backup
  // 如果不同，需要调整 BASE_PATH 或单独处理
  return request.get(`/server/query`, { params }) // **** 确认服务器列表的 API 路径 ****
}

// **** 新增：测试连接 ****
export const testConnectionApi = (data) => {
  // data: urlTestDto { backupWay, sourceType?, backupMethod?, urlList, backupTarget }
  return request.post(`/backup/urlConnectTest`, data)
}

// **** 新增：获取目标服务器列表 ****
export const getTargetServerListForBackup = () => {
  return request.get(`/backup/targetServer`)
}

// --- 其他可能需要的接口 (根据你的后端控制器添加) ---
// export const getBackupTypeList = () => get('/backup/getBackupTypeList');
// export const getDataBaseList = () => get('/backup/getDataBaseList');
// export const testUrlConnect = (data) => post('/backup/urlConnectTest', data);
// export const selectTables = (url) => get('/backup/selectTables', { url });
