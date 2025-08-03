<template>
  <div class="backup-module">
    <el-tabs v-model="activeTabName" type="card" @tab-change="handleTabChange">
      <el-tab-pane label="Backup Policies" name="strategy"></el-tab-pane>
      <el-tab-pane label="Backup History" name="history"></el-tab-pane>
    </el-tabs>
    <div class="tab-content">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElTabs, ElTabPane } from 'element-plus'

const route = useRoute()
const router = useRouter()
const activeTabName = ref('strategy')

watch(
  () => route.name,
  (newRouteName) => {
    if (newRouteName === 'backup-strategy') {
      activeTabName.value = 'strategy'
    } else if (newRouteName === 'backup-history') {
      activeTabName.value = 'history'
    }
  },
  { immediate: true },
)

const handleTabChange = (tabName) => {
  if (tabName === 'strategy') {
    router.push({ name: 'backup-strategy' })
  } else if (tabName === 'history') {
    router.push({ name: 'backup-history' })
  }
}
</script>

<style scoped>
.backup-module {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.el-tabs {
  flex-shrink: 0;
  background-color: #fff;
  padding: 0 15px;
  border-bottom: 1px solid #e4e7ed;
}
:deep(.el-tabs--card > .el-tabs__header) {
  border-bottom: none;
  margin: 0;
}
:deep(.el-tabs--card > .el-tabs__header .el-tabs__nav) {
  border: none;
}
:deep(.el-tabs--card > .el-tabs__header .el-tabs__item) {
  border-bottom: none;
  border-left: none;
  margin-top: 10px;
}
:deep(.el-tabs--card > .el-tabs__header .el-tabs__item.is-active) {
  border-bottom: 2px solid var(--el-color-primary);
  background-color: #fff;
}
.tab-content {
  flex-grow: 1;
  padding: 15px;
  background-color: #f5f7fa;
  overflow: auto;
}
</style>
