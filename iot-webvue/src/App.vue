<template>
  <div id="app">
    <div class="route-progress"></div>
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  overflow-x: hidden;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}
#app {
  height: 100%;
}
.route-progress {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background: #3E77FC;
  transform: scaleX(0);
  transform-origin: left center;
  opacity: 0;
  z-index: 9999;
  pointer-events: none;
  transition: opacity 0.2s ease;
}
body.route-progress-active .route-progress {
  opacity: 1;
  animation: route-progress-run 1.2s ease-in-out infinite;
}
@keyframes route-progress-run {
  0% {
    transform: scaleX(0);
  }
  60% {
    transform: scaleX(0.8);
  }
  100% {
    transform: scaleX(0.98);
  }
}
</style>
