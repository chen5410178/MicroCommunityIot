/**
 * 驾驶舱引擎
 */

var scene = null;
var sceneId = null;
var communityId = null;

export function initEvent() {
    vc.on('cockpitCore', 'loadRoomModal', function (_data) {
        loadRooms(_data);
    })
}

/**
 * 计算引擎
 * @param {场景} _scene 
 * @param {sceneId,communityId} _param
  */
export function computeSceneObject(_scene, _param) {
    scene = _scene;
    sceneId = _param.sceneId;
    communityId = _param.communityId;
    //todo 
    loadSceneObjectScript({
        page: 1,
        row: 100,
        communityId: communityId,
        sceneId: sceneId,
    })
        .then((scripts) => {
            scripts.forEach(_script => {
                try {
                    doScript(_script);
                } catch (err) {
                    console.error('执行脚本出错', err, _script);
                }
            });

        });
    _scene.setSelect3DObject((_object) => {
        console.log('selectObjName=', _object.name);

        let _objName = _object.name;

        if (_objName.endsWith('_focus')) {
            loadSceneObjectScript({
                page: 1,
                row: 1,
                communityId: communityId,
                sceneId: sceneId,
                objName: _objName.replace('_focus', '')
            }).then((scripts) => {
                if (scripts[0].objType == 'floor') {
                    scene.lookAtObject(scene.getObjectByName(_objName));
                    vc.emit('cockpit', 'curType', {
                        curType: 'floor',
                        data: scripts[0]
                    })
                    return;
                }

            });
            return;
        }

        if (_objName.endsWith('_text')) {
            scene.lookAtObject(scene.getObjectByName(_objName));
            let _roomId = _objName.split('_')[2];

            vc.emit('cockpit', 'curType',{
                curType: 'room',
                data: {
                    roomId:_roomId
                }
            })

        }


    })
}

function laodModal(_params) {
    let param = {
        params: _params
    };
    return new Promise((resolve, reject) => {
        //发送get请求
        vc.http.apiGet('/dtModal.listDtModal',
            param,
            function (json, res) {
                let _json = JSON.parse(json);
                if (!_json.data || _json.data.length < 1) {
                    reject('未包含数据');
                    return;
                }
                resolve(_json.data)
            }, function (errInfo, error) {
                console.log('请求失败处理');
            }
        );
    })
}

/**
 * 执行绑定的脚本
 * @param {单个对象脚本} _script 
 */
function doScript(_script) {

    let objName = _script.objName;
    if (!objName) {
        return;
    }

    let object = scene.getObjectByName(objName);

    if (!object) {
        return;
    }

    // 计算目标物体的中心位置  
    let _objectTarget = scene.getObjectPosition(object);
    let _height = scene.getObjectHeight(object) + 15;

    scene.addConicalShape(objName + "_focus");
    let objectFocus = scene.getObjectByName(objName + "_focus");

    objectFocus.position.set(_objectTarget.x, _objectTarget.y + _height, _objectTarget.z);

    scene.addShap(objectFocus);

    console.log(_script);
}

/**
 * 加载脚本对象
 * @returns 对象绑定脚本
 */
function loadSceneObjectScript(_params) {
    let param = {
        params: _params
    };
    return new Promise((resolve, reject) => {
        //发送get请求
        vc.http.apiGet('/dtScript.listDtSceneObjScript',
            param,
            function (json, res) {
                let _json = JSON.parse(json);
                if (!_json.data || _json.data.length < 1) {
                    reject('未包含数据');
                    return;
                }
                resolve(_json.data)
            }, function (errInfo, error) {
                console.log('请求失败处理');
            }
        );
    })

}

export function loadData(_params, _url) {
    let param = {
        params: _params
    };
    return new Promise((resolve, reject) => {
        //发送get请求
        vc.http.apiGet(_url,
            param,
            function (json, res) {
                let _json = JSON.parse(json);
                if (!_json.data || _json.data.length < 1) {
                    reject('未包含数据');
                    return;
                }
                resolve(_json.data)
            }, function (errInfo, error) {
                console.log('请求失败处理');
            }
        );
    })
}

/**
 * 查询房屋
 */
function loadRooms(_data) {

    let rooms = [];

    loadData({
        page: 1,
        row: 100,
        floorId: _data.floorId,
        layer: _data.layer,
        communityId: vc.getCurrentCommunity().communityId
    }, '/room.queryRooms')
        .then((_rooms) => {
            rooms = _rooms;
            return laodModal({
                page: 1,
                row: 1,
                modalType: 'floor',
                modalCode: 'layer_' + rooms.length
            })
        }).then((_modal) => {
            changeRoomModal(rooms, _modal[0])
        })
}

function changeRoomModal(_rooms, _modal) {
    scene.resetScene();
    scene.addGltfObject({
        path: _modal.path
    }).then(() => { // todo 加载场景完成 执行
        for (let _roomIndex = 1; _roomIndex < _rooms.length + 1; _roomIndex++) {
            try {
                computeRoomObject(_rooms[_roomIndex - 1], 'room_' + _roomIndex);
            } catch (err) {
                console.log('处理房屋对象失败', err);
            }
        }

    })
}

function computeRoomObject(_room, _objName) {


    let object = scene.getObjectByName(_objName);

    if (!object) {
        return;
    }

    // 计算目标物体的中心位置  
    let _objectTarget = scene.getObjectPosition(object);
    let _height = scene.getObjectHeight(object);
    _objectTarget.y = _objectTarget.y + _height
    _objectTarget.x = _objectTarget.x - 20;
    scene.addText(_objName + "_" + _room.roomId + "_text", _room.roomName, _objectTarget)




    // let objectFocus = scene.getObjectByName(_objName+"_text");

    // objectFocus.position.set(_objectTarget.x, _objectTarget.y + _height, _objectTarget.z);

}