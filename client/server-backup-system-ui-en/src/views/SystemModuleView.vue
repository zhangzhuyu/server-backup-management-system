<template>
  <div class="system-module-container">
    <!-- 这里可以放系统模块通用的布局或导航，如果需要的话 -->
    <router-view></router-view>
    <!-- 用于显示子路由组件 -->
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  console.log('系统模块已挂载')
})
</script>

<style scoped>
h2 {
  margin-bottom: 15px;
}
</style>
