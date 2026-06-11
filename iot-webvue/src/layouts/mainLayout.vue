<template>
  <div class="main-layout">
    <nav2 @load-menu="onLoadMenu" />
    <div class="vc-page">
      <menu2 ref="menu2" :catalog="currentCatalog" />
      <div class="vc-main">
        <div class="vc-main-content">
          <breadcrumb />
          <div class="vc-wrapper vc-content">
            <router-view />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Nav2 from '@/components/frame/nav2/nav2.vue'
import Menu2 from '@/components/frame/menu2/menu2.vue'
import Breadcrumb from '@/components/frame/breadcrumb/breadcrumb.vue'

export default {
  name: 'MainLayout',
  components: { Nav2, Menu2, Breadcrumb },
  data() {
    return {
      currentCatalog: null
    }
  },
  methods: {
    onLoadMenu(catalog) {
      this.currentCatalog = catalog
      this.$nextTick(() => {
        if (this.$refs.menu2) {
          this.$refs.menu2.getMenus(catalog)
        }
      })
    }
  }
}
</script>

<style scoped>
.main-layout {
  height: 100%;
  display: flex;
  flex-direction: column;
}
.vc-page {
  flex: 1;
  display: flex;
  overflow: hidden;
}
.vc-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.vc-main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.vc-wrapper {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f0f2f5;
}
</style>
