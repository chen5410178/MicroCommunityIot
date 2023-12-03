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
                sceneId: '',
                communityId: '',
                datas: [],
            }
        },
        _initMethod: function () {
            $that.cockpitInfo.communityId = vc.getCurrentCommunity().communityId;
            let _scene = new Cockpit3d('webgl-scene');
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
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);