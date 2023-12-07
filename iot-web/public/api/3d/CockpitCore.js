/**
 * 驾驶舱引擎
 */

var scene = null;
var sceneId = null;
var communityId = null;

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
    loadSceneObjectScript()
        .then((scripts) => {
            scripts.forEach(_script => {
                try {
                    doScript(_script);
                } catch (err) {
                    console.error('执行脚本出错', err, _script);
                }
            });

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
    let _height = scene.getObjectHeight(object)+15;

    scene.addConicalShape(objName + "_focus");
    let objectFocus = scene.getObjectByName(objName + "_focus");

    objectFocus.position.set(_objectTarget.x, _objectTarget.y+_height, _objectTarget.z);

    scene.addShap(objectFocus);

    console.log(_script);
}

/**
 * 加载脚本对象
 * @returns 对象绑定脚本
 */
function loadSceneObjectScript() {
    let param = {
        params: {
            page: 1,
            row: 100,
            communityId: communityId,
            sceneId: sceneId,
        }
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