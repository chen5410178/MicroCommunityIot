<template>
  <div class="vc-nav">
    <div class="nav-left">
      <ul class="nav-menu">
        <li class="title">
          <a @click.prevent="goHome" style="cursor:pointer">{{ systemInfo.logo || 'HCIOT' }}</a>
        </li>
        <li
          v-for="(item, index) in catalogs"
          :key="index"
          :class="{ active: item.active == '1' }"
          @click="changeCatalog(item)"
        >
          <i :class="item.icon"></i>{{ item.name }}
        </li>
      </ul>
    </div>
    <div class="nav-right">
      <ul class="nav-actions">
        <li v-if="canShowCommunity" class="dropdown">
          <span class="dropdown-toggle">
            {{ (currentCommunity && currentCommunity.name) || '暂无小区' }}
            <i class="el-icon-arrow-down el-icon--right"></i>
          </span>
          <div class="dropdown-menu">
            <a
              v-for="(c, index) in communityList"
              :key="index"
              class="dropdown-item"
              @click="changeCommunity(c)"
            >{{ c.name }}</a>
          </div>
        </li>
        <li><i class="el-icon-user"></i> {{ userInfo.name || '' }}</li>
        <li class="dropdown">
          <span class="dropdown-toggle">
            {{ langName }}
          </span>
          <ul class="dropdown-menu">
            <li v-for="(lang, index) in langs" :key="index">
              <a class="dropdown-item" @click="changeLang(lang)">{{ lang.name }}</a>
            </li>
          </ul>
        </li>
        <li @click="handleLogout">
          <i class="el-icon-switch-button"></i> 退出
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { listCatalog } from '@/api/menu/menuApi'
import { getUserInfo } from '@/api/user/userApi'
import { mapState } from 'vuex'

export default {
  name: 'Nav2',
  data() {
    return {
      catalogs: [],
      langs: [
        { name: '中文', lang: 'zh-cn' },
        { name: '繁体', lang: 'cn' },
        { name: 'English', lang: 'en' }
      ],
      langName: '中文'
    }
  },
  computed: {
    ...mapState('app', ['systemInfo', 'currentCommunity', 'communityList']),
    ...mapState('user', ['userInfo']),
    canShowCommunity() {
      const storeTypeCd = this.userInfo.storeTypeCd
      return storeTypeCd !== '800900000001' && storeTypeCd !== '800900000002' && storeTypeCd !== '800900000000'
    }
  },
  mounted() {
    this.loadCatalogs()
    this.loadUserInfo()
  },
  methods: {
    loadCatalogs() {
      listCatalog({ page: 1, row: 10, isShow: 'Y' }).then(res => {
        let data = res
        if (data && data.data) {
          data = data.data
        }
        if (Array.isArray(data) && data.length > 0) {
          data[0].active = '1'
        }
        this.catalogs = data || []
        this.$store.commit('app/SET_CATALOGS', this.catalogs)
        if (data && data.length > 0) {
          this.$emit('load-menu', data[0])
        }
      }).catch(() => {})
    },
    loadUserInfo() {
      getUserInfo().then(res => {
        let userInfo = res
        if (typeof userInfo === 'string') {
          try { userInfo = JSON.parse(userInfo) } catch (e) {}
        }
        if (userInfo && userInfo.data) {
          userInfo = userInfo.data
        }
        this.$store.commit('user/SET_USER_INFO', userInfo)
      }).catch(() => {})
    },
    changeCatalog(catalog) {
      if (catalog.url && catalog.url !== '#') {
        if (catalog.url.startsWith('http')) {
          window.open(catalog.url)
          return
        }
      }
      this.catalogs.forEach(item => { item.active = '0' })
      catalog.active = '1'
      this.$emit('load-menu', catalog)
    },
    changeCommunity(community) {
      this.$store.commit('app/SET_CURRENT_COMMUNITY', community)
      this.$router.go(0)
    },
    changeLang(lang) {
      this.langName = lang.name
      this.$store.commit('app/SET_LANGUAGE', lang.lang)
      sessionStorage.setItem('iot_webvue_lang', lang.lang)
      location.reload()
    },
    goHome() {
      this.$router.push('/pages/index/index')
    },
    handleLogout() {
      this.$store.dispatch('user/logout').then(() => {
        this.$router.push('/user')
      })
    }
  }
}
</script>

<style scoped>
.vc-nav {
  height: 50px;
  background: #001529;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 16px;
  flex-shrink: 0;
  overflow: hidden;
}
.nav-left, .nav-right {
  overflow: hidden;
}
.nav-left ul, .nav-right ul {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  align-items: center;
}
.nav-menu li, .nav-actions li {
  padding: 0 12px;
  height: 50px;
  line-height: 50px;
  cursor: pointer;
  color: rgba(255,255,255,0.65);
  white-space: nowrap;
}
.nav-menu li:hover, .nav-actions li:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}
.nav-menu li.active {
  color: #fff;
  background: #1890ff;
}
.nav-menu li.title {
  font-weight: bold;
  font-size: 16px;
  color: #fff;
  padding-right: 24px;
}
.nav-menu li.title a {
  color: #fff;
  text-decoration: none;
}
.dropdown {
  position: relative;
}
.dropdown-menu {
  display: none;
  position: absolute;
  top: 50px;
  left: 0;
  background: #fff;
  color: #333;
  min-width: 120px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  border-radius: 4px;
  z-index: 1000;
  padding: 4px 0;
}
.dropdown:hover .dropdown-menu {
  display: block;
}
.dropdown-item {
  display: block;
  padding: 8px 16px;
  cursor: pointer;
  color: #333;
  text-decoration: none;
  white-space: nowrap;
}
.dropdown-item:hover {
  background: #f5f5f5;
}
</style>
