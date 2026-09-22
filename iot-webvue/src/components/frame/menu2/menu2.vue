<template>
  <div class="vc-menu">
    <div class="vc-menu-main">
      <ul>
        <li
          v-for="(menu, index) in menus"
          :key="index"
          v-if="menu.childs && menu.childs.length > 0"
          :class="{ active: menu.active }"
          @click="switchMenu(menu)"
        >
          <i :class="menu.icon"></i><span class="menu-name">{{ menu.name }}</span>
        </li>
      </ul>
    </div>
    <div class="vc-menu-sub" v-if="subMenus && subMenus.length > 0">
      <ul>
        <li class="sub-title">{{ curMenuName }}</li>
        <li
          v-for="(sub, index) in subMenus"
          :key="index"
          v-if="sub.isShow === 'Y'"
          :class="{ active: sub.active }"
          @click="gotoPage(sub.href, sub.name)"
          @mouseenter="onMenuHover(sub)"
        >
          {{ sub.name }}
        </li>
        <li class="sub-footer" @click="closeSubMenu">
          <i class="el-icon-back"></i>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { listCatalogMenus } from '@/api/menu/menuApi'
import { getCurrentCommunity, setMenus } from '@/utils/auth'
import { prefetchOnIdle, prefetchRoutePath, collectMenuPaths, normalizeMenuPath } from '@/utils/routePrefetch'

export default {
  name: 'Menu2',
  data() {
    return {
      menus: [],
      subMenus: [],
      curMenuName: ''
    }
  },
  watch: {
    '$route'() {
      this.syncActiveByRoute()
    }
  },
  methods: {
    getMenus(catalog) {
      if (!catalog || !catalog.caId) return
      listCatalogMenus({
        caId: catalog.caId,
        communityId: (getCurrentCommunity() || {}).communityId || ''
      }).then(res => {
        let data = res
        if (data && data.data) {
          data = data.data
        }
        if (!data || data.length === 0) return
        data.sort((a, b) => a.seq - b.seq)
        this.menus = this.refreshMenuActive(data, '')
        setMenus(this.menus)
        this.syncActiveByRoute()
        this.prefetchMenus()
      }).catch(() => {})
    },
    prefetchMenus() {
      prefetchOnIdle(this.$router, collectMenuPaths(this.menus), { budget: 6 })
    },
    onMenuHover(sub) {
      prefetchRoutePath(this.$router, normalizeMenuPath(sub.href))
    },
    refreshMenuActive(menus, activeId) {
      menus.forEach(menu => {
        if (menu.childs) {
          menu.childs.sort((a, b) => a.seq - b.seq)
        }
        menu.active = menu.id === activeId
      })
      return menus
    },
    switchMenu(menu) {
      this.menus = this.refreshMenuActive(this.menus, menu.id).slice()
      setMenus(this.menus)
      this.subMenus = menu.childs || []
      this.curMenuName = menu.name
      if (menu.childs && menu.childs.length > 0) {
        this.gotoPage(menu.childs[0].href, menu.childs[0].name)
      }
    },
    gotoPage(href, tabName) {
      const targetPath = href ? href.replace(/^\/?#\//, '/') : ''
      this.subMenus.forEach(sub => {
        const subPath = sub.href ? sub.href.replace(/^\/?#\//, '/') : ''
        sub.active = subPath === targetPath
      })
      this.menus.forEach(menu => {
        if (menu.childs && menu.childs.some(c => {
          const childPath = c.href ? c.href.replace(/^\/?#\//, '/') : ''
          return childPath === targetPath
        })) {
          menu.active = true
        } else {
          menu.active = false
        }
      })
      if (targetPath) {
        this.$router.push(targetPath).catch(() => {})
      }
    },
    syncActiveByRoute() {
      const currentPath = this.$route.path
      this.menus.forEach(menu => {
        menu.active = false
        if (menu.childs) {
          menu.childs.forEach(sub => { sub.active = false })
        }
      })
      for (const menu of this.menus) {
        if (menu.childs) {
          const matched = menu.childs.find(c => {
            const childPath = c.href ? c.href.replace(/^\/?#\//, '/') : ''
            return childPath === currentPath
          })
          if (matched) {
            menu.active = true
            this.subMenus = menu.childs
            this.curMenuName = menu.name
            this.subMenus.forEach(sub => {
              sub.active = sub.href === matched.href
            })
            break
          }
        }
      }
    },
    closeSubMenu() {
      this.subMenus = []
      this.curMenuName = ''
    }
  }
}
</script>

<style scoped>
.vc-menu {
  display: flex;
  height: 100%;
  flex-shrink: 0;
  border-right: 1px solid #EFEFF0;
}
.vc-menu-main {
  width: 80px;
  background: #1F2339;
  color: #838A9B;
  text-align: center;
  font-size: 14px;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
}
.vc-menu-main::-webkit-scrollbar {
  display: none;
}
.vc-menu-main ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.vc-menu-main li {
  height: 45px;
  line-height: 45px;
  cursor: pointer;
  margin: 0;
  padding: 0 8px;
  text-align: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.vc-menu-main li:hover {
  background: #3E77FC;
  color: #FFFFFF;
}
.vc-menu-main li.active {
  background: #3E77FC;
  color: #FFFFFF;
}
.vc-menu-main li i {
  font-size: 18px;
}
.menu-name {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 60px;
  display: inline-block;
  vertical-align: middle;
  margin-left: 4px;
}
.vc-menu-sub {
  width: 110px;
  background: #FFFFFF;
  color: #545761;
  font-size: 14px;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: none;
  position: relative;
}
.vc-menu-sub::-webkit-scrollbar {
  display: none;
}
.vc-menu-sub ul {
  list-style: none;
  margin: 0;
  padding: 0;
  padding-bottom: 40px;
}
.vc-menu-sub li {
  height: 40px;
  line-height: 40px;
  padding-left: 15px;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.vc-menu-sub li.sub-title {
  color: #4F525C;
  height: 40px;
  line-height: 40px;
  font-size: 14px;
  text-align: center;
  padding-left: 0;
  border-bottom: 1px solid #F2F2F3;
  margin-bottom: 10px;
  cursor: default;
}
.vc-menu-sub li:hover {
  background: #EAF0FE;
  color: #3E77FC;
}
.vc-menu-sub li.active {
  background: #EAF0FE;
  color: #3E77FC;
}
.vc-menu-sub li.sub-title:hover {
  background: transparent;
  color: #4F525C;
}
.vc-menu-sub li.sub-footer {
  background: #F6F6F7;
  text-align: center;
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  padding-left: 0;
}
.vc-menu-sub li.sub-footer:hover {
  background: #EAF0FE;
  color: #3E77FC;
}
</style>
