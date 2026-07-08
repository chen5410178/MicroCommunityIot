<template>
  <div class="vc-nav">
    <div class="nav-left">
      <div class="nav-logo" @click="goHome">{{ systemInfo.logo || 'HCIOT' }}</div>
      <div class="nav-catalog-menu">
        <span
          v-for="(item, index) in catalogs"
          :key="index"
          :class="{ 'catalog-item': true, 'is-active': activeCatalog === item.caId }"
          @click="handleCatalogSelect(item.caId)"
        >
          <i v-if="item.icon" :class="item.icon"></i>{{ item.name }}
        </span>
      </div>
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
import { listSystemInfo } from '@/api/login/systemApi'
import { userLogout } from '@/api/login/loginApi'
import {
  setSystemInfo,
  getCurrentCommunity, setCurrentCommunity,
  getCommunityList, setCommunityList,
  setUserInfo,
  clearAll
} from '@/utils/auth'

export default {
  name: 'Nav2',
  data() {
    return {
      systemInfo: {logo:''},
      userInfo: {},
      catalogs: [],
      activeCatalog: '',
      language: 'zh-cn',
      langs: [
        { name: '中文', lang: 'zh-cn' },
        { name: '繁体', lang: 'cn' },
        { name: 'English', lang: 'en' }
      ],
      langName: '中文'
    }
  },
  computed: {
    currentCommunity() { return getCurrentCommunity() || {} },
    communityList() { return getCommunityList() || [] },
    canShowCommunity() {
      const storeTypeCd = this.userInfo.storeTypeCd
      return storeTypeCd !== '800900000001' && storeTypeCd !== '800900000002' && storeTypeCd !== '800900000000'
    }
  },
  mounted() {
    this.loadSystemInfo()
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
        this.userInfo = userInfo
        setUserInfo(userInfo)
      }).catch(() => {})
    },
    loadSystemInfo() {
      listSystemInfo({ page: 1, row: 1 }).then(res => {
        let data = JSON.parse(JSON.stringify(res))
        console.log('systemInfo', data)
        if (data && data.data && data.data.length > 0) {
          const sysInfo = data.data[0]
          this.systemInfo = {
            ...sysInfo,
            logo: sysInfo.systemSimpleTitle || sysInfo.systemTitle
          }
          setSystemInfo(this.systemInfo)
        }
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
      setCurrentCommunity(community)
      this.$router.go(0)
    },
    handleSwitchLang(command) {
      const lang = this.langs.find(l => l.lang === command)
      if (lang) {
        this.langName = lang.name
      }
      this.language = command
      sessionStorage.setItem('iot_webvue_lang', command)
      location.reload()
    },
    goHome() {
      this.$router.push('/pages/index/index')
    },
    handleUserCommand(command) {
      if (command === 'logout') {
        userLogout({ msg: 123 }).then(() => {
          clearAll()
          this.$router.push('/pages/frame/login')
        }).catch(() => {
          clearAll()
          this.$router.push('/pages/frame/login')
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
.nav-logo {
  width: 100px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
  cursor: pointer;
}
.nav-catalog-menu {
  display: flex;
  align-items: center;
  height: 100%;
  overflow: hidden;
}

.catalog-item {
  height: 60px;
  line-height: 60px;
  padding: 0 20px;
  color: #fff;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
  border-bottom: 2px solid transparent;
  transition: border-color 0.3s, background-color 0.3s;
}

.catalog-item:hover {
  background-color: rgba(255, 255, 255, 0.08);
}

.catalog-item.is-active {
  border-bottom-color: #409EFF;
  color: #409EFF;
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
