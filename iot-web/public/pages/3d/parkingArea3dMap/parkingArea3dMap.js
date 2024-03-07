/**
    入驻小区
**/
import ParkingAreaMap3d from 'api/3d/ParkingAreaMap3d.js';
(function(vc) {

    vc.extends({
        data: {
            parkingArea3dMapInfo: {
                scene: {},
                sceneId: '',
                communityId: '',
            }
        },
        _initMethod: function() {
            $that.parkingArea3dMapInfo.communityId = vc.getCurrentCommunity().communityId;
            let _scene = new ParkingAreaMap3d('webgl-scene');
            // _scene.addJpgBackgroup({
            //     path: '/img/3d_bg.jpg'
            // })
            $that.parkingArea3dMapInfo.scene = _scene;
            let _pSpaces = [];
            for(let i= 0; i< 530;i++){
                _pSpaces.push({
                    psNum:(i+1),
                    psName:(i+1)+"车位"
                })
            }

            _scene.initParkingSpace(_pSpaces);
        },
        _initEvent: function() {

          
        },
        methods: {
            
        }
    });
})(window.vc);