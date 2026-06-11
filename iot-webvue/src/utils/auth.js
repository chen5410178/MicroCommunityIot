const TOKEN_KEY = 'iot_webvue_token'
const USER_KEY = 'iot_webvue_user'
const COMMUNITY_KEY = 'iot_webvue_current_community'
const COMMUNITY_LIST_KEY = 'iot_webvue_community_list'
const MENUS_KEY = 'iot_webvue_menus'
const TABS_KEY = 'iot_webvue_tabs'

export function getToken() {
  return sessionStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  sessionStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  sessionStorage.removeItem(TOKEN_KEY)
}

export function getUserInfo() {
  const user = sessionStorage.getItem(USER_KEY)
  return user ? JSON.parse(user) : null
}

export function setUserInfo(userInfo) {
  sessionStorage.setItem(USER_KEY, JSON.stringify(userInfo))
}

export function removeUserInfo() {
  sessionStorage.removeItem(USER_KEY)
}

export function getCurrentCommunity() {
  const community = sessionStorage.getItem(COMMUNITY_KEY)
  return community ? JSON.parse(community) : null
}

export function setCurrentCommunity(community) {
  sessionStorage.setItem(COMMUNITY_KEY, JSON.stringify(community))
}

export function getCommunityList() {
  const list = sessionStorage.getItem(COMMUNITY_LIST_KEY)
  return list ? JSON.parse(list) : []
}

export function setCommunityList(list) {
  sessionStorage.setItem(COMMUNITY_LIST_KEY, JSON.stringify(list))
}

export function getMenus() {
  const menus = sessionStorage.getItem(MENUS_KEY)
  return menus ? JSON.parse(menus) : []
}

export function setMenus(menus) {
  sessionStorage.setItem(MENUS_KEY, JSON.stringify(menus))
}

export function getTabs() {
  const tabs = sessionStorage.getItem(TABS_KEY)
  return tabs ? JSON.parse(tabs) : []
}

export function setTabs(tabs) {
  sessionStorage.setItem(TABS_KEY, JSON.stringify(tabs))
}

export function clearAll() {
  sessionStorage.clear()
}
