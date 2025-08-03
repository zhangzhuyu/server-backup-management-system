// src/api/backupHistory.js
import { get, download } from './axiosInstance'

/**
 * 获取备份历史列表
 * @param {object} params - 查询参数 { page, pageSize, title?, totalMethod?, value?, strategyId? }
 * 'value' 对应目录树 ID，如果不用目录树可以忽略
 * 新增 strategyId 用于策略详情页跳转和直接过滤
 * @returns {Promise<IPage<LyDbBackupHistoryRecordPo>>}
 */
export const getHistoryList = (params) => {
  // 如果同时有 value 和 strategyId，需要确认后端如何处理优先级
  // 这里假设 strategyId 优先
  const queryParams = { ...params }
  if (queryParams.strategyId) {
    delete queryParams.value // 移除目录树参数，避免冲突
  }
  return get('/backup-history/backup-history/list', queryParams)
}

/**
 * 根据历史记录 ID 获取备份详情
 * @param {number|string} id - 备份历史记录 ID
 * @returns {Promise<LyDbBackupHistoryDetailsVo>}
 */
export const getHistoryDetail = (id) => get(`/backup-history/backup-history/details/${id}`)

/**
 * 下载备份文件
 * @param {object} params - { id?, strategyId? }
 */
export const downloadBackupFile = (params) => {
  let filenamePrefix = 'backup'
  if (params.id) {
    filenamePrefix = `backup_history_${params.id}`
  } else if (params.strategyId) {
    filenamePrefix = `backup_strategy_${params.strategyId}_latest`
  }
  // 注意：download 是我们 axiosInstance 里封装的下载函数
  return download('/backup-history/download-backup-file', params, 'GET', filenamePrefix)
}

/**
 * 获取用于悬浮球/实时进度的备份详情 (假设这是轮询接口)
 * @returns {Promise<LyDbBackupLevitatedSphereVo>}
 */
export const getDailyBackupDetails = () =>
  get('/backup-history/backup-history/levitated-sphere/details')

// --- 其他可能需要的接口 ---
// export const getBackupTree = () => get('/backup-history/find-backup-tree');
// export const getHistoryListByStrategy = (id, params) => get(`/backup-history/backup-history/get/${id}`, params); // 如果需要这个特定接口
