<template>
  <div class="vc-nav">
    <div class="nav-left">
      <div class="logo" @click="goHome">{{ systemInfo.logo || 'HCIOT' }}</div>
      <el-menu
        mode="horizontal"
        :default-active="activeCatalog"
        class="nav-catalog-menu"
        background-color="#1e2132"
        text-color="#fff"
        active-text-color="#409EFF"
        @select="handleCatalogSelect"
      >
        <el-menu-item
          v-for="(item, index) in catalogs"
          :key="index"
          :index="item.caId"
        >
          <i v-if="item.icon" :class="item.icon"></i>{{ item.name }}
        </el-menu-item>
      </el-menu>
    </div>
    <div class="nav-right">
      <el-dropdown @command="handleSwitchLang" class="nav-dropdown">
        <span class="nav-user-text">
          <i class="el-icon-global"></i> {{ langName }}
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="zh-cn">中文</el-dropdown-item>
          <el-dropdown-item command="cn">繁体</el-dropdown-item>
          <el-dropdown-item command="en">English</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-dropdown
        v-if="canShowCommunity"
        @command="handleCommunityCmd"
        class="nav-dropdown"
      >
        <span class="nav-user-text">
          {{ (currentCommunity && currentCommunity.name) || '暂无小区' }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(c, index) in communityList"
            :key="index"
            :command="c"
          >{{ c.name }}</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-dropdown @command="handleUserCommand" class="nav-dropdown">
        <span class="nav-user-text">
          {{ userInfo.name || '' }}
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="logout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
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
      activeCatalog: '',
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
          this.activeCatalog = data[0].caId
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
    handleCatalogSelect(index) {
      const catalog = this.catalogs.find(c => c.caId === index)
      if (!catalog) return
      if (catalog.url && catalog.url !== '#') {
        if (catalog.url !== 'IOT' && catalog.url !== 'MALL') {
          window.open(catalog.url)
        }
      }
      this.catalogs.forEach(item => { item.active = '0' })
      catalog.active = '1'
      this.activeCatalog = index
      this.$emit('load-menu', catalog)
    },
    handleCommunityCmd(community) {
      this.$store.commit('app/SET_CURRENT_COMMUNITY', community)
      this.$router.go(0)
    },
    handleSwitchLang(command) {
      const lang = this.langs.find(l => l.lang === command)
      if (lang) {
        this.langName = lang.name
      }
      this.$store.commit('app/SET_LANGUAGE', command)
      sessionStorage.setItem('iot_webvue_lang', command)
      location.reload()
    },
    goHome() {
      this.$router.push('/pages/index/index')
    },
    handleUserCommand(command) {
      if (command === 'logout') {
        this.$store.dispatch('user/logout').then(() => {
          this.$router.push('/user')
        })
      }
    }
  }
}
</script>

<style scoped>
.vc-nav {
  height: 60px;
  background: #1e2132;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
  flex-shrink: 0;
  overflow: hidden;
}
.nav-left {
  display: flex;
  align-items: center;
  height: 100%;
}
.logo {
  width: 100px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;
  flex-shrink: 0;
}
.nav-catalog-menu {
  border-bottom: none !important;
  background: transparent;
}
.nav-catalog-menu >>> .el-menu-item {
  height: 60px;
  line-height: 60px;
  border-bottom: none !important;
}
.nav-catalog-menu >>> .el-menu-item:hover {
  background-color: rgba(255,255,255,0.08) !important;
}
.nav-catalog-menu >>> .el-menu-item.is-active {
  border-bottom: 2px solid #409EFF !important;
}
.nav-right {
  display: flex;
  align-items: center;
  margin-right: 20px;
  gap: 8px;
}
.nav-dropdown {
  cursor: pointer;
}
.nav-user-text {
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  padding: 0 8px;
}
.nav-user-text:hover {
  color: #409EFF;
}
</style>
