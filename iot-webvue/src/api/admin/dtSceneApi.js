import request from '@/utils/request'

export function listDtScenes(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScene.listDtScene',
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

export function saveDtScene(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScene.saveDtScene',
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

export function updateDtScene(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScene.updateDtScene',
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

export function deleteDtScene(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScene.deleteDtScene',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listDtSceneObjScripts(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScript.listDtSceneObjScript',
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

export function deleteDtSceneObjScript(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtScript.deleteDtSceneObjScript',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

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
