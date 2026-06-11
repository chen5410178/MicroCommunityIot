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
          <i :class="menu.icon"></i>
          <span class="menu-name">{{ menu.name }}</span>
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
        >
          {{ sub.name }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { listCatalogMenus } from '@/api/menu/menuApi'

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
        communityId: this.$store.state.app.currentCommunity.communityId
      }).then(res => {
        let data = res
        if (data && data.data) {
          data = data.data
        }
        if (!data || data.length === 0) return
        data.sort((a, b) => a.seq - b.seq)
        this.menus = this.refreshMenuActive(data, '')
        this.$store.commit('app/SET_MENUS', this.menus)
        this.syncActiveByRoute()
      }).catch(() => {})
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
      this.menus = this.refreshMenuActive(this.menus, menu.id)
      this.$store.commit('app/SET_MENUS', this.menus)
      this.subMenus = menu.childs || []
      this.curMenuName = menu.name
      if (menu.childs && menu.childs.length > 0) {
        this.gotoPage(menu.childs[0].href, menu.childs[0].name)
      }
    },
    gotoPage(href, tabName) {
      this.subMenus.forEach(sub => {
        sub.active = sub.href === href
      })
      const path = href ? href.replace(/^\/?#\//, '/') : ''
      this.menus.forEach(menu => {
        if (menu.childs && menu.childs.some(c => c.href === href)) {
          menu.active = true
        } else {
          menu.active = false
        }
      })
      if (path) {
        this.$router.push(path).catch(() => {})
      }
    },
    syncActiveByRoute() {
      const currentPath = this.$route.path
      this.menus.forEach(menu => {
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
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.vc-menu {
  display: flex;
  height: 100%;
  flex-shrink: 0;
}
.vc-menu-main {
  width: 80px;
  background: #001529;
  overflow-y: auto;
}
.vc-menu-main ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.vc-menu-main li {
  padding: 16px 8px;
  text-align: center;
  color: rgba(255,255,255,0.65);
  cursor: pointer;
  font-size: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}
.vc-menu-main li:hover {
  color: #fff;
  background: rgba(255,255,255,0.1);
}
.vc-menu-main li.active {
  color: #fff;
  background: #1890ff;
}
.vc-menu-main li i {
  font-size: 18px;
}
.menu-name {
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 72px;
}
.vc-menu-sub {
  width: 200px;
  background: #fafafa;
  border-right: 1px solid #e8e8e8;
  overflow-y: auto;
}
.vc-menu-sub ul {
  list-style: none;
  margin: 0;
  padding: 0;
}
.vc-menu-sub .sub-title {
  padding: 16px;
  font-weight: bold;
  font-size: 14px;
  color: #333;
  border-bottom: 1px solid #e8e8e8;
}
.vc-menu-sub li {
  padding: 12px 16px;
  cursor: pointer;
  color: #666;
  font-size: 13px;
}
.vc-menu-sub li:hover {
  color: #1890ff;
  background: #e6f7ff;
}
.vc-menu-sub li.active {
  color: #1890ff;
  background: #e6f7ff;
  border-right: 3px solid #1890ff;
}
</style>
