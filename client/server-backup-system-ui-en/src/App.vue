<template>
  <!-- RouterView 会根据当前路由显示对应的组件 -->
  <RouterView />
</template>

<script setup>
import { RouterView } from 'vue-router'
// App.vue 通常不需要太多逻辑，路由和布局由其他组件管理
</script>

<style>
/* 全局样式可以放在这里，或者在 assets/base.css 中 */
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0; /* 移除默认的 body margin */
  padding: 0;
}

/* 确保根元素占满视口 */
html,
body,
#app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>
