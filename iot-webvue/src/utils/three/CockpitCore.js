import Vue from 'vue'
import { listDtSceneObjScripts, listDtModals, queryRooms } from '@/api/3d/cockpitApi'

const eventBus = new Vue()

var scene = null
var sceneId = null
var communityId = null
var floorId = null
var layer = null

export function initEvent() {
  eventBus.$on('cockpitCore.loadRoomModal', function (_data) {
    loadRooms(_data)
  })
}

export function computeSceneObject(_scene, _param) {
  scene = _scene
  sceneId = _param.sceneId
  communityId = _param.communityId

  loadSceneObjectScript({
    page: 1,
    row: 100,
    communityId: communityId,
    sceneId: sceneId
  }).then((scripts) => {
    scripts.forEach(_script => {
      try {
        doScript(_script)
      } catch (err) {
        console.error('执行脚本出错', err, _script)
      }
    })
  })

  _scene.setSelect3DObject((_object) => {
    console.log('selectObjName=', _object.name)
    let _objName = _object.name

    if (_objName.endsWith('_focus')) {
      loadSceneObjectScript({
        page: 1,
        row: 1,
        communityId: communityId,
        sceneId: sceneId,
        objName: _objName.replace('_focus', '')
      }).then((scripts) => {
        if (scripts[0] && scripts[0].objType == 'floor') {
          scene.lookAtObject(scene.getObjectByName(_objName))
          eventBus.$emit('cockpit.curType', {
            curType: 'floor',
            data: scripts[0]
          })
        }
      })
      return
    }

    if (_objName.endsWith('_text')) {
      scene.lookAtObject(scene.getObjectByName(_objName))
      let _roomId = _objName.split('_')[2]
      eventBus.$emit('cockpit.curType', {
        curType: 'room',
        data: {
          roomId: _roomId,
          floorId: floorId,
          layer: layer
        }
      })
    }
  })
}

function laodModal(_params) {
  return listDtModals(_params).then((res) => {
    if (!res.data || res.data.length < 1) {
      return Promise.reject('未包含数据')
    }
    return res.data
  })
}

function doScript(_script) {
  let objName = _script.objName
  if (!objName) {
    return
  }
  let object = scene.getObjectByName(objName)
  if (!object) {
    return
  }
  let _objectTarget = scene.getObjectPosition(object)
  let _height = scene.getObjectHeight(object) + 15

  scene.addConicalShape(objName + '_focus')
  let objectFocus = scene.getObjectByName(objName + '_focus')
  objectFocus.position.set(_objectTarget.x, _objectTarget.y + _height, _objectTarget.z)
  scene.addShap(objectFocus)
  console.log(_script)
}

function loadSceneObjectScript(_params) {
  return listDtSceneObjScripts(_params).then((res) => {
    if (!res.data || res.data.length < 1) {
      return Promise.reject('未包含数据')
    }
    return res.data
  })
}

function loadRooms(_data) {
  let rooms = []
  floorId = _data.floorId
  layer = _data.layer

  queryRooms({
    page: 1,
    row: 100,
    floorId: _data.floorId,
    layer: _data.layer,
    communityId: communityId
  }).then((res) => {
    rooms = res.data || []
    return laodModal({
      page: 1,
      row: 1,
      modalType: 'floor',
      modalCode: 'layer_' + rooms.length
    })
  }).then((_modal) => {
    changeRoomModal(rooms, _modal[0])
  }).catch((err) => {
    console.error('未查询到房屋', err)
  })
}

function changeRoomModal(_rooms, _modal) {
  scene.resetScene()
  scene.addGltfObject({
    path: _modal.path
  }).then(() => {
    for (let _roomIndex = 1; _roomIndex < _rooms.length + 1; _roomIndex++) {
      try {
        computeRoomObject(_rooms[_roomIndex - 1], 'room_' + _roomIndex)
      } catch (err) {
        console.log('处理房屋对象失败', err)
      }
    }
  })
}

function computeRoomObject(_room, _objName) {
  let object = scene.getObjectByName(_objName)
  if (!object) {
    return
  }
  let _objectTarget = scene.getObjectPosition(object)
  let _height = scene.getObjectHeight(object)
  _objectTarget.y = _objectTarget.y + _height
  _objectTarget.x = _objectTarget.x - 20
  scene.addText(_objName + '_' + _room.roomId + '_text', _room.roomName, _objectTarget)
}

export { eventBus }
