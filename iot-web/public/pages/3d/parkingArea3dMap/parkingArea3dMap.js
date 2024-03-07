/**
    入驻小区
**/
import ParkingAreaMap3d from 'api/3d/ParkingAreaMap3d.js';
(function(vc) {

    vc.extends({
        data: {
            parkingArea3dMapInfo: {
                scene: {},
                regions:[],
                parkingSpaces:[],
                prId:'',
                paId:'',
                sceneId: '',
                communityId: '',
            }
        },
        _initMethod: function() {
            $that.parkingArea3dMapInfo.communityId = vc.getCurrentCommunity().communityId;
            $that.parkingArea3dMapInfo.paId = vc.getParam('paId');
            $that._queryParkingRegions();
            let _scene = new ParkingAreaMap3d('webgl-scene');
            // _scene.addJpgBackgroup({
            //     path: '/img/3d_bg.jpg'
            // })
            $that.parkingArea3dMapInfo.scene = _scene;
        },
        _initEvent: function() {

          
        },
        methods: {
            swatchParkingRegion:function(_region){
                $that.parkingArea3dMapInfo.prId = _region.prId;
                $that._listParkingSpaces();

            },
            _listParkingSpaces: function(_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:1000,
                        communityId:vc.getCurrentCommunity().communityId,
                        prId:$that.parkingArea3dMapInfo.prId
                    }
                };

                //发送get请求
                vc.http.apiGet('/parkingSpace.listParkingSpace',
                    param,
                    function(json, res) {
                        let _parkingSpaceInfo = JSON.parse(json);
                        $that.parkingArea3dMapInfo.parkingSpaces = _parkingSpaceInfo.data;

                        $that.computeParkingStateColor();
                
                        if(_parkingSpaceInfo.data && _parkingSpaceInfo.data.length>0){
                            $that.parkingArea3dMapInfo.scene.initParkingSpace($that.parkingArea3dMapInfo.parkingSpaces);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryParkingRegions: function(_page, _rows) {
                if(!$that.parkingArea3dMapInfo.paId){
                    return;
                }
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                        paId:$that.parkingArea3dMapInfo.paId
                    }
                };
                //发送get请求
                vc.http.apiGet('/parkingRegion.listParkingRegion',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingArea3dMapInfo.regions = _json.data;
                        if(_json.data && _json.data.length>0){
                            $that.swatchParkingRegion(_json.data[0]);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            computeParkingStateColor:function(){
                let _parkingSpaces = $that.parkingArea3dMapInfo.parkingSpaces;
                _parkingSpaces.forEach(item => {
                    if(item.psState == 'FREE'){
                        item.psColor = 0x00FF00;
                    }else if(item.psState == 'OCCUPY'){
                        item.psColor = 0xFF0000;
                    }else{
                        item.psColor = 0x0000FF;
                    }
                });
            }
        }
    });
})(window.vc);