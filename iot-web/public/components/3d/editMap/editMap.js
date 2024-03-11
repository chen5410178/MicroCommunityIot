/**
    入驻小区
**/

(function (vc) {

    vc.extends({
        data: {
            editMapInfo: {
                latLng: {},
                editMap:{}
            }
        },
        _initMethod: function () {


        },
        _initEvent: function () {

            vc.on('editMap', 'showMap', function (_param) {
                $that.editMapInfo.latLng = _param;
                if($that.editMapInfo.editMap.destroy){
                    $that.editMapInfo.editMap.destroy();
                }
                $that._showEditMapContainer();
            })

        },
        methods: {
            _showEditMapContainer: function () {

                let _community = vc.getCurrentCommunity();
                let _lat = 39.916527;
                let _lng = 116.397128;
                if (_community && _community.lat) {
                    _lat = _community.lat;
                }
                if (_community && _community.lng) {
                    _lng = _community.lng;
                }

                let latLng = $that.editMapInfo.latLng;
                if(latLng.lat){
                    _lat = latLng.lat;
                }
                if(latLng.lng){
                    _lng = latLng.lng;
                }
                let editMap = new TMap.Map(document.getElementById("editMapContainer"), {
                    center: new TMap.LatLng(_lat, _lng),
                    zoom: 13
                });
                $that.editMapInfo.editMap = editMap;
                let marker;
                marker = new TMap.MultiMarker({
                    map: editMap,
                    id:"1",
                });
                marker.updateGeometries([
                    {
                     "styleId":"marker",
                     "id": "1",
                     "position":new TMap.LatLng(_lat, _lng),
                    }
                ])
                //添加监听事件  获取鼠标点击事件
                editMap.on('click', function (event) {
                    //marker.setPosition(event.latLng);
                    $that.editMapInfo.latLng.lat = event.latLng.lat;
                    $that.editMapInfo.latLng.lng = event.latLng.lng;

                    marker.updateGeometries([
                        {
                         "styleId":"marker",
                         "id": "1",
                         "position": event.latLng,
                        }
                    ])
                });
            }
        }
    });
})(window.vc);