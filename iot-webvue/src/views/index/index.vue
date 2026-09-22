<template>
  <div v-if="loading" v-loading="loading" style="min-height:400px"></div>
  <div v-else>
    <propertyDashboard v-if="storeTypeCd === '800900000003'" />
    <adminDashboard v-if="storeTypeCd === '800900000001'" />
    <devDashboard v-if="storeTypeCd === '800900000000'" />
  </div>
</template>

<script>
import { getUserInfo } from '@/api/user/userApi'
import { setUserInfo as saveUserInfo } from '@/utils/auth'

export default {
  name: 'Dashboard',
  components: {
    propertyDashboard: () => import(/* webpackChunkName: "dashboard-property" */ './propertyDashboard.vue'),
    adminDashboard: () => import(/* webpackChunkName: "dashboard-admin" */ './adminDashboard.vue'),
    devDashboard: () => import(/* webpackChunkName: "dashboard-dev" */ './devDashboard.vue')
  },
  data() {
    return {
      loading: true,
      storeTypeCd: ''
    }
  },
  mounted() {
    this.fetchUserType()
  },
  methods: {
    async fetchUserType() {
      try {
        const res = await getUserInfo()
        let userInfo = res
        if (typeof userInfo === 'string') {
          try { userInfo = JSON.parse(userInfo) } catch (e) {}
        }
        if (userInfo && userInfo.data) {
          userInfo = userInfo.data
        }
        this.storeTypeCd = userInfo.storeTypeCd || ''
        saveUserInfo(userInfo)
      } catch {
        this.storeTypeCd = ''
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
