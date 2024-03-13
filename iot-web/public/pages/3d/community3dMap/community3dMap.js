/**
    入驻小区
**/
import Community3d from 'api/3d/Community3d.js';
(function(vc) {

    vc.extends({
        data: {
            community3dMapInfo: {
                scene: {},
                floors:[],
              
                sceneId: '',
                communityId: '',
            }
        },
        _initMethod: function() {
            $that.community3dMapInfo.communityId = vc.getCurrentCommunity().communityId;
            let _scene = new Community3d('webgl-scene');
            // _scene.addJpgBackgroup({
            //     path: '/img/3d_bg.jpg'
            // })
            $that.community3dMapInfo.scene = _scene;
            $that._loadFloors(_scene);
        },
        _initEvent: function() {

        },
        methods: {
            _loadFloors: function(_scene) {
                let param = {
                    params: {
                        page:1,
                        row:1000,
                        communityId:vc.getCurrentCommunity().communityId,
                    }
                };

                //发送get请求
                vc.http.apiGet('/floor.queryFloors',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.community3dMapInfo.floors = _json.data;
                        _scene.initFloors(_json.data);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);