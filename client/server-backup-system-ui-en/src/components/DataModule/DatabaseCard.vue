<template>
  <el-card shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="resource-name">{{ database.dataSourceName }}</span>
        <el-tag :type="sourceTypeTag(database.sourceType).type" size="small">
          {{ sourceTypeTag(database.sourceType).label }}
        </el-tag>
      </div>
    </template>
    <div class="card-body">
      <p><strong>IP:</strong> {{ database.ip || 'N/A' }}</p>
      <p>
        <strong>Connection Status:</strong>
        <el-tag :type="testStatusTag(database.testStatus).type" size="small" effect="light">
          {{ testStatusTag(database.testStatus).label }}
        </el-tag>
      </p>
    </div>
    <div class="card-footer">
      <el-button
        type="primary"
        link
        size="small"
        @click="$emit('test-connection', database.id)"
        :loading="testing"
        >Test Connection</el-button
      >
      <el-button type="warning" link size="small" @click="$emit('edit', database)">Edit</el-button>
      <el-button type="danger" link size="small" @click="$emit('delete', database.id)"
        >Delete</el-button
      >
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElCard, ElTag, ElButton } from 'element-plus'

const props = defineProps({
  database: {
    type: Object,
    required: true,
  },
})

defineEmits(['edit', 'delete', 'test-connection'])

const testing = ref(false)

// Computes the tag for the database type
const sourceTypeTag = (type) => {
  switch (String(type)) {
    case 'Oracle':
      return { label: 'Oracle', type: 'success' }
    case 'MySQL':
      return { label: 'MySQL', type: 'warning' }
    case '6':
      return { label: 'Mongo', type: 'danger' }
    // Add other types...
    default:
      return { label: 'Unknown', type: 'info' }
  }
}

// Computes the tag for the connection test status
const testStatusTag = (status) => {
  // Backend uses 1 for success, 0 for failure
  switch (String(status)) {
    case '1':
      return { label: 'Success', type: 'success' }
    case '0':
      return { label: 'Failed', type: 'danger' }
    default:
      return { label: 'Not Tested', type: 'info' }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.resource-name {
  font-weight: bold;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}
.card-body p {
  margin: 5px 0;
  font-size: 13px;
  color: #606266;
}
.card-footer {
  margin-top: 10px;
  border-top: 1px solid #e4e7ed;
  padding-top: 10px;
  display: flex;
  justify-content: flex-end;
}
.el-button + .el-button {
  margin-left: 8px;
}
</style>
