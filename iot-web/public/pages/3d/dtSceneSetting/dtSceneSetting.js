/**
    入驻小区
**/

//import {initScene,add3DObject} from 'api/3d/sceneApi.js';
import Scene3d from 'api/3d/Scene3d.js'
(function (vc) {
    vc.extends({
        data: {
            dtSceneSettingInfo: {
                scene: {},
                modal: 'translate',
                object: {}
            }
        },
        _initMethod: function () {
            //$that.dtSceneSettingInfo.scene = initScene("webgl-scene",$that.dtSceneSettingInfo);
            //addBoxGeometry("webgl-scene",$that.dtSceneSettingInfo);

            let _scene = new Scene3d('webgl-scene');
            $that.dtSceneSettingInfo.scene = _scene;

            _scene.setSelect3DObject(function (_object) {
                console.log('选中objetc', _object);
                vc.emit('dtSceneSetting', 'controlObject', _object);
            })
        },
        _initEvent: function () {
            vc.on('dtSceneSetting', 'addModal', function (_modal) {
                if (_modal.path == 'AmbientLight') {
                    $that.dtSceneSettingInfo.scene.initLight();
                    return;
                }
                if(_modal.path.indexOf('.hdr')>0){
                    $that.dtSceneSettingInfo.scene.addHdrBackgroup(_modal);
                    return;
                }
                if(_modal.path.indexOf('.jpg')>0){
                    $that.dtSceneSettingInfo.scene.addJpgBackgroup(_modal);
                    return;
                }
                $that.dtSceneSettingInfo.scene.addGltfObject(_modal);
                //$that.dtSceneSettingInfo.scene.add3DBox(_modal);
            });
            vc.on('dtSceneSetting', 'deleteModal', function (_object) {
                $that.dtSceneSettingInfo.scene.deleteObject(_object);

            });
            vc.on('dtSceneSetting', 'setModal', function (_modal) {
                $that.dtSceneSettingInfo.modal = _modal;
                $that.dtSceneSettingInfo.scene.setTransformControlModal(_modal);
            });
            vc.on('dtSceneSetting', 'getAllObject', function (_objects) {
                let _objs = $that.dtSceneSettingInfo.scene.getAllObject();
                _objs.forEach(_o => {
                    _objects.push(_o);
                });
            });

            vc.on('dtSceneSetting', 'controlObject', function (_object) {
                $that.dtSceneSettingInfo.object = _object;
                vc.emit('curModal', 'setModalData', {
                    name: _object.name,
                    position: _object.position,
                    object: _object
                });
                $that.dtSceneSettingInfo.scene.setTransformControlModal($that.dtSceneSettingInfo.modal);
                $that.dtSceneSettingInfo.scene.controlObject(_object);
            });
            vc.on('dtSceneSetting', 'saveScene', function () {
                let _sceneJson = $that.dtSceneSettingInfo.scene.exportScene();
                $that._saveSceneJson(_sceneJson);
            });


        },
        methods: {
            _saveSceneJson: function (_sceneJson) {
                let _data = {
                    sceneId:vc.getParam('sceneId'),
                    communityId:vc.getParam('communityId'),
                    modalJson:_sceneJson
                }
                vc.http.apiPost(
                    '/dtScene.updateDtScene',
                    JSON.stringify(_data),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        vc.toast(_json.msg);
                        if (_json.code == 0) {
                            //关闭model
                            window.close();
                            return;
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            }
        }
    });
})(window.vc);