/**
    入驻小区
**/
import Cockpit3d from 'api/3d/Cockpit3d.js'
(function (vc) {
    vc.extends({
        data: {
            cockpitInfo: {
                scene: {},
                sceneId: '',
                communityId: '',
                datas: [],

            }
        },
        _initMethod: function () {
            $that.cockpitInfo.communityId = vc.getCurrentCommunity().communityId;
            let _scene = new Cockpit3d('webgl-scene');
            _scene.addJpgBackgroup({
                path:'/img/3d_bg.jpg'
            })
            $that.cockpitInfo.scene = _scene;
            $that._listDtScenes();
        },
        _initEvent: function () {

        },
        methods: {
            _listDtScenes: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 15,
                        communityId: $that.cockpitInfo.communityId,
                        sceneId: $that.cockpitInfo.sceneId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/dtScene.listDtScene',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.cockpitInfo.datas = _json.data;
                        if (!_json.data || _json.data.length < 1) {
                            return;
                        }
                        $that._changeScene(_json.data[0])
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _changeScene: function (_data) {
                $that.cockpitInfo.sceneId = _data.sceneId;
                if(!_data.modalJson){
                    return;
                }
                let _scene = $that.cockpitInfo.scene;
                _scene.resetScene();
                _scene.addGltfObject({
                    path:_data.modalJson
                });


            }
        }
    });
})(window.vc);