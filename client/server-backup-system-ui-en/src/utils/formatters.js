// src/utils/formatters.js
import { h } from 'vue'
import { ElTag, ElProgress } from 'element-plus'
import dayjs from 'dayjs'

// --- Data Maps ---

// Maps backup method codes (total_method)
export const backupMethodMap = {
  1: 'MySQLDump',
  2: 'EXP (Oracle)',
  3: 'EXPDP (Oracle)',
  4: 'MongoDump',
  5: 'HTTP',
  6: 'SSH/SFTP',
  7: 'FTP',
  8: 'CIFS/SMB',
  9: 'CDC',
}

// Maps policy last status (last_status) from the policy table
const strategyLastStatusMap = {
  0: { text: 'Running', type: 'primary' },
  1: { text: 'Succeeded', type: 'success' },
  2: { text: 'Failed', type: 'danger' },
  3: { text: 'Stopped', type: 'warning' },
  4: { text: 'Scheduled', type: 'info' },
}

// Maps policy enabled status (enable)
export const strategyEnableMap = {
  1: { text: 'Enabled', type: 'success' },
  0: { text: 'Disabled', type: 'info' },
  2: { text: 'Paused', type: 'warning' },
}

// Maps backup history status (backup_status) from the history table
export const historyStatusMap = {
  0: { text: 'Running', type: 'primary', progressing: true },
  1: { text: 'Succeeded', type: 'success' },
  2: { text: 'Failed', type: 'danger' },
  3: { text: 'Stopped', type: 'warning' },
  4: { text: 'Pending', type: 'info' },
}

// Maps execution mode (task_mode)
export const taskModeMap = {
  1: 'Immediate',
  2: 'Scheduled',
}

// --- Filter Generators ---

// Helper function to generate filter arrays from map objects
const mapToFilters = (mapObject) => {
  return Object.entries(mapObject).map(([value, details]) => ({
    text: typeof details === 'object' ? details.text : details,
    value: String(value),
  }))
}

export const backupMethodFilters = mapToFilters(backupMethodMap)
export const strategyLastStatusFilters = mapToFilters(strategyLastStatusMap)
export const taskModeFilters = mapToFilters(taskModeMap)

// --- Formatting Functions ---

/**
 * Formats the backup method (total_method).
 * @param {string} methodCode
 * @returns {string | null}
 */
export const formatBackupMethod = (methodCode) => {
  return backupMethodMap[methodCode] || methodCode || null
}

/**
 * Formats the policy's last status (last_status) as an ElTag.
 * @param {string} statusCode
 * @returns {VNode | null}
 */
// export const formatStrategyLastStatus = (statusCode) => {
//   const codeStr = statusCode !== null && statusCode !== undefined ? String(statusCode) : ''
//   const status = strategyLastStatusMap[codeStr]

//   if (status) {
//     return h(ElTag, { type: status.type, size: 'small', effect: 'light' }, () => status.text)
//   }
//   return null
// }
export const formatStrategyLastStatus = (statusCode, taskMode) => {
  // ====================== 演示规则 ======================
  // 如果执行模式是 'Scheduled' (值为 '2')，则强制显示 'Scheduled' 状态
  if (String(taskMode) === '2') {
    const scheduledStatus = strategyLastStatusMap['4'] // '4' 是 Scheduled
    if (scheduledStatus) {
      return h(
        ElTag,
        { type: scheduledStatus.type, size: 'small', effect: 'light' },
        () => scheduledStatus.text,
      )
    }
  }
  // =====================================================

  // 如果不符合演示规则，则按原逻辑执行
  const codeStr = statusCode !== null && statusCode !== undefined ? String(statusCode) : ''
  const status = strategyLastStatusMap[codeStr]

  if (status) {
    return h(ElTag, { type: status.type, size: 'small', effect: 'light' }, () => status.text)
  }
  return null
}

/**
 * Formats the policy's enabled status (enable) as an ElTag.
 * @param {string} enableCode
 * @returns {VNode | null}
 */
export const formatStrategyEnable = (enableCode) => {
  const status = strategyEnableMap[enableCode]
  if (status) {
    return h(ElTag, { type: status.type, size: 'small', effect: 'light' }, () => status.text)
  }
  return enableCode || null
}

/**
 * Formats the backup history status (backup_status) with an optional progress bar.
 * @param {string} statusCode
 * @param {string | number} progress (from the 'proportion' field)
 * @returns {VNode | null}
 */
export const formatHistoryStatus = (statusCode, progress) => {
  const status = historyStatusMap[statusCode]
  if (!status) {
    return statusCode || null
  }

  const progressValue = progress ? Number(progress) : 0
  const showProgress = status.progressing && progress !== undefined && progress !== null

  return h('div', { style: 'display: flex; align-items: center; gap: 8px;' }, [
    h(ElTag, { type: status.type, size: 'small', effect: 'light' }, () => status.text),
    showProgress
      ? h(ElProgress, {
          percentage: progressValue,
          'stroke-width': 6,
          style: { width: '80px' },
          status:
            status.type === 'danger'
              ? 'exception'
              : status.type === 'success'
                ? 'success'
                : status.type === 'warning'
                  ? 'warning'
                  : undefined,
          'show-text': false,
        })
      : null,
  ])
}

/**
 * Formats a date-time string using dayjs.
 * @param {string | Date} dateTimeString
 * @param {string} format - The desired output format.
 * @returns {string | null}
 */
export const formatDateTime = (dateTimeString, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!dateTimeString) return null
  const date = dayjs(dateTimeString)
  // Return the formatted date if valid, otherwise return the original string
  return date.isValid() ? date.format(format) : dateTimeString
}

/**
 * Formats the execution mode (task_mode).
 * @param {string} modeCode
 * @returns {string | null}
 */
export const formatTaskMode = (modeCode) => {
  return taskModeMap[modeCode] || modeCode || null
}
