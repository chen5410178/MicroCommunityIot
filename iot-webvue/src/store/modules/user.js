import { getUserInfo, setUserInfo, removeUserInfo, clearAll } from '@/utils/auth'
import { pcUserLogin, userLogout } from '@/api/login/loginApi'
import { getUserInfo as fetchUserInfo } from '@/api/user/userApi'

const state = {
  token: '',
  userInfo: getUserInfo() || {},
  privileges: []
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    setUserInfo(userInfo)
  },
  SET_PRIVILEGES(state, privileges) {
    state.privileges = privileges
  },
  CLEAR_USER(state) {
    state.token = ''
    state.userInfo = {}
    state.privileges = []
    removeUserInfo()
  }
}

const actions = {
  login({ commit }, loginData) {
    return new Promise((resolve, reject) => {
      pcUserLogin(loginData).then(res => {
        commit('SET_USER_INFO', { username: loginData.username })
        resolve(res)
      }).catch(error => {
        reject(error)
      })
    })
  },
  getUserInfoAction({ commit }) {
    return new Promise((resolve, reject) => {
      fetchUserInfo().then(res => {
        let userInfo = res
        if (typeof res === 'string') {
          userInfo = JSON.parse(res)
        }
        if (userInfo.data) {
          userInfo = userInfo.data
        }
        commit('SET_USER_INFO', userInfo)
        resolve(userInfo)
      }).catch(error => {
        reject(error)
      })
    })
  },
  logout({ commit }) {
    return new Promise((resolve, reject) => {
      userLogout({ msg: 123 }).then(() => {
        commit('CLEAR_USER')
        clearAll()
        resolve()
      }).catch(() => {
        commit('CLEAR_USER')
        clearAll()
        resolve()
      })
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
