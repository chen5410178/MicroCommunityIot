/**
    入驻小区
**/
import Cockpit3d from 'api/3d/Cockpit3d.js'
(function (vc) {
    vc.extends({
        data: {
            cockpitInfo: {
                scene: {},
                modal: 'translate',
                object: {},
                sceneId: '',
                communityId: '',
            }
        },
        _initMethod: function () {
            let _scene = new Cockpit3d('webgl-scene');
            $that.cockpitInfo.scene = _scene;
        },
        _initEvent: function () {
           
        },
        methods: {
            
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