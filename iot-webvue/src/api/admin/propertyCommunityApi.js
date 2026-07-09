import request from '@/utils/request'

export function listCommunitys(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.listCommunitys',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function joinCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/member.join.community',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function quitCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/member.quit.community',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateMenuGroupCommunity(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/menuGroupCommunity.updateMenuGroupCommunity',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '操作失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listMenuGroup(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/menuGroup.listMenuGroup',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listFreeCommunity(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/community.listFreeCommunity',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listMenuGroupCommunity(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/menuGroupCommunity.listMenuGroupCommunity',
      method: 'get',
      params
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询列表失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
