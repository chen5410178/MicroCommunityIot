<template>
  <component :is="dashboardComponent" />
</template>

<script>
import { getUserInfo } from '@/utils/auth'
import propertyDashboard from './propertyDashboard.vue'
import adminDashboard from './adminDashboard.vue'
import devDashboard from './devDashboard.vue'

export default {
  name: 'Dashboard',
  components: { propertyDashboard, adminDashboard, devDashboard },
  computed: {
    userInfo() { return getUserInfo() || {} },
    dashboardComponent() {
      const map = {
        '800900000003': propertyDashboard,
        '800900000001': adminDashboard,
        '800900000000': devDashboard
      }
      return map[this.userInfo.storeTypeCd] || propertyDashboard
    }
  }
}
</script>
