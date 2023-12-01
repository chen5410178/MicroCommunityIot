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
                object: {},
                sceneId: '',
                communityId: '',
            }
        },
        _initMethod: function () {
            $that.dtSceneSettingInfo.sceneId = vc.getParam('sceneId');
            $that.dtSceneSettingInfo.communityId = vc.getParam('communityId');

            let _scene = new Scene3d('webgl-scene');
            $that.dtSceneSettingInfo.scene = _scene;

            _scene.setSelect3DObject(function (_object) {
                vc.emit('dtSceneSetting', 'controlObject', _object);
            });
            $that._listDtScenes();
        },
        _initEvent: function () {
            vc.on('dtSceneSetting', 'addModal', function (_modal) {
                if (_modal.path == 'AmbientLight') {
                    $that.dtSceneSettingInfo.scene.initLight();
                    return;
                }
                if (_modal.path.indexOf('.hdr') > 0) {
                    $that.dtSceneSettingInfo.scene.addHdrBackgroup(_modal);
                    return;
                }
                if (_modal.path.indexOf('.jpg') > 0) {
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
                vc.emit('sceneModal', 'notifyObjects', $that.dtSceneSettingInfo.scene.getObjectTree());
            });

            vc.on('dtSceneSetting', 'controlObject', function (_object) {
                _object = $that.dtSceneSettingInfo.scene.getObject(_object.id);
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
                $that.dtSceneSettingInfo.scene.exporterGlb().then(_obj => {
                    $that._saveSceneJson(_obj);
                })
            });
        },
        methods: {
            _saveSceneJson: function (blob) {
                let param = new FormData();
                param.append("uploadFile", blob);
                //发送get请求
                vc.http.upload('/upload/uploadFile',
                    param, {
                    emulateJSON: true,
                    //添加请求头
                    headers: {
                        "Content-Type": "multipart/form-data"
                    },
                    progress: function (event) {
                        let rate = event.loaded / event.total; //已上传的比例
                        if (rate < 0.9) {
                            //this.uploadFileInfo.progress = (rate * 100).toFixed(2);
                        }
                    }
                },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        _json = _json.data;
                        $that._updateScene(_json.url);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理', error);
                        vc.toast("上传文件失败");
                    });
            },
            _updateScene: function (_url) {
                if (!_url) {
                    return;
                }
                let _data = {
                    sceneId: vc.getParam('sceneId'),
                    communityId: vc.getParam('communityId'),
                    modalJson: _url
                }
                vc.http.apiPost(
                    '/dtScene.updateDtScene',
                    JSON.stringify(_data),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
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
            },
            _listDtScenes: function () {
                let _scene = $that.dtSceneSettingInfo.scene;
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        communityId: $that.dtSceneSettingInfo.communityId,
                        sceneId: $that.dtSceneSettingInfo.sceneId,
                    }
                };
                let _url = "";
                //发送get请求
                vc.http.apiGet('/dtScene.listDtScene',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        _url = _json.data[0].modalJson;
                        if (_url.indexOf('http') < 0) {
                            return;
                        }
                        _scene.addGltfObject({
                            path: _url
                        })

                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);