<template>
  <div class="breadcrumb" v-if="breadCrumbs.length > 0 && isIndexPage">
    <span
      v-for="(item, index) in breadCrumbs"
      :key="index"
      class="breadcrumb-item"
      :class="{ active: item.active === '1' }"
      @click="changeTab(item)"
    >
      {{ item.pageName }}
      <i class="el-icon-close" @click.stop="deleteTab(item)"></i>
    </span>
  </div>
</template>

<script>
import { getTabs, setTabs } from '@/utils/auth'

export default {
  name: 'Breadcrumb',
  data() {
    return {
      breadCrumbs: [],
      isIndexPage: true
    }
  },
  mounted() {
    this.freshBreadcrumb()
    this.$router.afterEach(() => {
      this.freshBreadcrumb()
    })
  },
  methods: {
    freshBreadcrumb() {
      const tabs = getTabs()
      const curPath = this.$route.path
      this.isIndexPage = curPath !== '/pages/index/index'
      this.breadCrumbs = tabs.map(item => ({
        href: item.url,
        pageName: item.name,
        active: item.url === curPath ? '1' : '0'
      }))
    },
    addTab(url, name) {
      let tabs = getTabs()
      const exist = tabs.find(t => t.url === url)
      if (!exist) {
        tabs.push({ url, name })
      }
      tabs.forEach(t => { t.active = t.url === url ? '1' : '0' })
      setTabs(tabs)
      this.freshBreadcrumb()
    },
    changeTab(item) {
      if (item.href) {
        this.$router.push(item.href).catch(() => {})
      }
    },
    deleteTab(item) {
      let tabs = getTabs()
      const idx = tabs.findIndex(t => t.url === item.href)
      if (idx > -1) {
        tabs.splice(idx, 1)
      }
      setTabs(tabs)
      if (item.active === '0') {
        this.freshBreadcrumb()
        return
      }
      if (tabs.length === 0) {
        this.$router.push('/').catch(() => {})
        return
      }
      this.$router.push(tabs[tabs.length - 1].url).catch(() => {})
    }
  }
}
</script>

<style scoped>
.breadcrumb {
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  flex-shrink: 0;
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}
.breadcrumb-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 12px;
  border-radius: 2px;
  cursor: pointer;
  color: #666;
  font-size: 13px;
  background: #f0f0f0;
}
.breadcrumb-item:hover {
  color: #1890ff;
}
.breadcrumb-item.active {
  color: #1890ff;
  background: #e6f7ff;
}
.breadcrumb-item .el-icon-close {
  font-size: 12px;
}
.breadcrumb-item .el-icon-close:hover {
  color: #f5222d;
}
</style>
