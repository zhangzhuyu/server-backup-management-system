<template>
  <el-card shadow="hover">
    <template #header>
      <div class="card-header">
        <span class="resource-name">{{ server.name }}</span>
      </div>
    </template>
    <div class="card-body">
      <p><strong>IP:</strong> {{ server.ipv4 || 'N/A' }}</p>
      <p>
        <strong>Connection Status:</strong>
        <el-tag :type="testStatusTag(server.testStatus).type" size="small" effect="light">
          {{ testStatusTag(server.testStatus).label }}
        </el-tag>
      </p>
    </div>
    <div class="card-footer">
      <el-button
        type="primary"
        link
        size="small"
        @click="$emit('test-connection', server.id)"
        :loading="isTesting"
      >
        Test Connection
      </el-button>
      <el-button type="warning" link size="small" @click="$emit('edit', server)">Edit</el-button>
      <el-button type="danger" link size="small" @click="$emit('delete', server.id)"
        >Delete</el-button
      >
    </div>
  </el-card>
</template>

<script setup>
import { ElCard, ElTag, ElButton } from 'element-plus'

const props = defineProps({
  server: {
    type: Object,
    required: true,
  },
  isTesting: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['edit', 'delete', 'test-connection'])

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
