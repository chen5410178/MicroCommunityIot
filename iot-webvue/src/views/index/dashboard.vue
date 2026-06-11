<template>
  <component :is="dashboardComponent" />
</template>

<script>
import { mapState } from 'vuex'
import propertyDashboard from './propertyDashboard.vue'
import adminDashboard from './adminDashboard.vue'
import devDashboard from './devDashboard.vue'

export default {
  name: 'Dashboard',
  components: { propertyDashboard, adminDashboard, devDashboard },
  computed: {
    ...mapState('user', ['userInfo']),
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
