/**
    入驻小区
**/

(function (vc) {

    vc.extends({
        data: {
            curModalInfo: {
                name: '',
                action: '',
                position: {},
                object: {},
                rotateX: 0,
                rotateY: 0,
                rotateZ: 0,
                scaleX: 0,
                scaleY: 0,
                scaleZ: 0,
                scriptId:'',
                scriptName:'',
            }
        },
        _initMethod: function () {


        },
        _initEvent: function () {
            vc.on('curModal', 'setModalData', function (_data) {
                $that.curModalInfo.name = _data.name;
                $that.curModalInfo.position = _data.position;
                $that.curModalInfo.object = _data.object;

                if (!$that.curModalInfo.action) {
                    $that.curModalInfo.action = 'translate';
                }

                $that._loadObjScript();
            });
            vc.on('curModal', 'loadObjScript', function () {
                $that._loadObjScript();
            })

        },
        methods: {
            _curModalMove: function () {
                $that.curModalInfo.action = 'translate';
                vc.emit('dtSceneSetting', 'setModal', $that.curModalInfo.action)

            },
            _curModalRotate: function () {
                $that.curModalInfo.action = 'rotate';
                vc.emit('dtSceneSetting', 'setModal', $that.curModalInfo.action)

            },
            _curModalScale: function () {
                $that.curModalInfo.action = 'scale';
                vc.emit('dtSceneSetting', 'setModal', $that.curModalInfo.action)

            },
            _deleteCurModal: function () {
                vc.emit('dtSceneSetting', 'deleteModal', $that.curModalInfo.object);
                $that.curModalInfo.name = '';
                $that.curModalInfo.action = '';
                vc.emit('sceneModal', '_reloadTab', {});
            },
            _submitRotate: function () {

                let _data = {
                    x: parseFloat($that.curModalInfo.rotateX) / 180 * Math.PI,
                    y: parseFloat($that.curModalInfo.rotateY) / 180 * Math.PI,
                    z: parseFloat($that.curModalInfo.rotateZ) / 180 * Math.PI,
                }
                $that.curModalInfo.object.rotation.set(_data.x, _data.y, _data.z, "XZY");
            },
            _submitScale: function () {
                $that.curModalInfo.object.scale.set($that.curModalInfo.scaleX, $that.curModalInfo.scaleY, $that.curModalInfo.scaleZ);
            },
            _submitScene: function () {
                vc.emit('dtSceneSetting', 'saveScene', {});

            },
            _addCurObjectScript: function () {
                vc.emit('objectScript', 'openModal', {
                    communityId: vc.getParam('communityId'),
                    sceneId: vc.getParam('sceneId'),
                    objName: $that.curModalInfo.name
                });
            },
            _loadObjScript: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        communityId: vc.getParam('communityId'),
                        sceneId: vc.getParam('sceneId'),
                        objName: $that.curModalInfo.name
                    }
                };
                $that.curModalInfo.scriptId = '';
                $that.curModalInfo.scriptName = '';
                //发送get请求
                vc.http.apiGet('/dtScript.listDtSceneObjScript',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if(_json.data.length>0){
                            $that.curModalInfo.scriptId = _json.data[0].scriptId;
                            $that.curModalInfo.scriptName = _json.data[0].scriptName;
                        }
                       
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }

        }
    });
})(window.vc);