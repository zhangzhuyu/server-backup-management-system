// src/api/targetServer.js
import { request } from './axiosInstance' // 确保导入了你的 Axios 实例

// 获取目标服务器列表 (对应 /backup/list GET，但你说不需要参数)
export const getTargetServerList = () => {
  // 如果后端确认需要 backupWay，应该像下面这样传：
  // return request.get('/backup/list', { params: { backupWay: 'some_value' } });
  // 既然你说不需要，就直接调用
  return request.get('/backup-management/list') // *** 确认这个路径是否正确！它来自 BackupController ***
  // !!! 严重怀疑：列表接口可能在 BackupController 下，而不是专门的 TargetServerController
  // !!! 请与后端确认目标服务器列表的正确 API 端点！
  // !!! 如果是 /backup/list，它期望的参数 backupWay 可能代表某种过滤，需要确认！
}

// 新增目标服务器 (对应 /backup/insert POST)
export const addTargetServer = (data) => {
  // data 是 BackupManagementDto 对象
  return request.post('/backup-management/insert', data) // *** 确认路径 /backup/insert ***
}

// 更新目标服务器 (对应 /backup/update POST)
export const updateTargetServer = (data) => {
  // data 是包含 id 的 BackupManagementDto 对象
  return request.post('/backup-management/update', data) // *** 确认路径 /backup/update ***
}

// 测试目标服务器连接 (对应 /backup/testConnection POST)
export const testTargetConnection = (data) => {
  // data 是 { id: serverId }
  return request.post('/backup-management/testConnection', data) // *** 确认路径 /backup/testConnection ***
  // !!! 注意：这个接口也在 BackupController 下，且接收 BackupManagementDto
  // !!! 它可能期望的不仅仅是 id，需要确认！
  // !!! 如果它只需要 id，后端需要能根据 id 查到完整信息来测试。
}

// 删除目标服务器 (对应 /backup/delete POST)
export const deleteTargetServer = (ids) => {
  // data 是 ID 数组 [id1, id2, ...]
  return request.post('/backup-management/delete', ids) // *** 确认路径 /backup/delete ***
  // !!! 注意：这个接口也在 BackupController 下
}

// ---- 重要提醒 ----
// 你提供的所有后端接口都在 BackupController 下，并且涉及 BackupManagementDto。
// 这意味着 "目标服务器" 可能并不是一个独立的实体，而是 "备份管理" 配置的一部分或另一种视角。
// 前端的 API 路径 (/backup/list, /backup/insert 等) 需要严格对应后端 Controller 的 Mapping。
// 请务必与后端开发人员确认这些 API 的确切用途、路径和期望的参数/请求体！
// 上面的 API 实现是基于你提供的信息进行的最佳猜测。
