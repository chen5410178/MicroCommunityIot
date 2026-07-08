import request from '@/utils/request'
import { getCommunityId } from '@/utils/auth'

export function queryOwners(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/owner.queryOwners',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询业主失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveOwner(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.saveOwner',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '添加人员失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateOwner(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.editOwner',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改人员失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteOwner(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.deleteOwner',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除人员失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryRoomsByOwner(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/room.queryRoomsByOwner',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function saveOwnerRoomRel(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.saveOwnerRoomRel',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '绑定房屋失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteOwnerRoomRel(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/owner.deleteOwnerRoomRel',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '解除绑定失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listOwnerMembers(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/owner.listOwnerMember',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询家庭成员失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryCommunityAccount(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/account.queryCommunityAccount',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询小区账户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function queryOwnerAccountDetail(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/account.queryOwnerAccountDetail',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询账户详情失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function ownerPrestoreAccount(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/account.ownerPrestoreAccount',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '预充值失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteAccount(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/account.deleteAccount',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除账户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function listAppUsers(params) {
  return new Promise((resolve, reject) => {
    params.communityId = getCommunityId()
    request({
      url: '/appUser.listAppUser',
      method: 'get',
      params
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '查询用户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function updateAppUser(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/appUser.updateAppUser',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '修改用户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}

export function deleteAppUser(data) {
  return new Promise((resolve, reject) => {
    data.communityId = getCommunityId()
    request({
      url: '/appUser.deleteAppUser',
      method: 'post',
      data
    }).then(res => {
      if (res.code === 0) {
        resolve(res)
      } else {
        reject(new Error(res.msg || '删除用户失败'))
      }
    }).catch(error => {
      reject(error)
    })
  })
}
