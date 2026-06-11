import {
  getCurrentCommunity, setCurrentCommunity,
  getCommunityList, setCommunityList,
  getMenus, setMenus
} from '@/utils/auth'

const state = {
  sidebar: { opened: true },
  systemInfo: { logo: 'HCIOT', systemTitle: '' },
  currentCommunity: getCurrentCommunity() || {},
  communityList: getCommunityList() || [],
  catalogs: [],
  menus: getMenus() || [],
  subMenus: [],
  language: 'zh-cn'
}

const mutations = {
  SET_SYSTEM_INFO(state, info) {
    state.systemInfo = info
  },
  SET_CURRENT_COMMUNITY(state, community) {
    state.currentCommunity = community
    setCurrentCommunity(community)
  },
  SET_COMMUNITY_LIST(state, list) {
    state.communityList = list
    setCommunityList(list)
  },
  SET_CATALOGS(state, catalogs) {
    state.catalogs = catalogs
  },
  SET_MENUS(state, menus) {
    state.menus = menus
    setMenus(menus)
  },
  SET_SUB_MENUS(state, subMenus) {
    state.subMenus = subMenus
  },
  SET_LANGUAGE(state, language) {
    state.language = language
  },
  TOGGLE_SIDEBAR(state) {
    state.sidebar.opened = !state.sidebar.opened
  }
}

const actions = {
  initApp({ commit, dispatch }) {
    return dispatch('user/getUserInfoAction', {}, { root: true }).then(userInfo => {
      return userInfo
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
