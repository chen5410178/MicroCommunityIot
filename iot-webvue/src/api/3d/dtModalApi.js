import request from '@/utils/request'

export function listDtModals(params) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtModal.listDtModal',
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

export function saveDtModal(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtModal.saveDtModal',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateDtModal(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtModal.updateDtModal',
      method: 'post',
      data
    }).then(res => {
      if (res.code == 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteDtModal(data) {
  return new Promise((resolve, reject) => {
    request({
      url: '/dtModal.deleteDtModal',
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
