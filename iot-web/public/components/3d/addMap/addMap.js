/**
    入驻小区
**/

(function (vc) {

    vc.extends({
        data: {
            addMapInfo: {
                latLng: {},
                addMap: {}

            }
        },
        _initMethod: function () {


        },
        _initEvent: function () {

            vc.on('addMap', 'showMap', function (_param) {
                $that.addMapInfo.latLng = _param;
                if ($that.addMapInfo.addMap.destroy) {
                    $that.addMapInfo.addMap.destroy();
                }
                $that._showAddMapContainer();
            })

        },
        methods: {
            _showAddMapContainer: function () {

                let _community = vc.getCurrentCommunity();
                let _lat = 39.916527;
                let _lng = 116.397128;
                if (_community && _community.lat) {
                    _lat = _community.lat;
                }
                if (_community && _community.lng) {
                    _lng = _community.lng;
                }

                let addMap = new TMap.Map(document.getElementById("addMapContainer"), {
                    center: new TMap.LatLng(_lat, _lng),
                    zoom: 13
                });
                $that.addMapInfo.addMap = addMap;
                let marker;
                //添加监听事件  获取鼠标点击事件
                addMap.on('click', function (event) {
                    if (!marker) {
                        marker = new TMap.MultiMarker({
                            styleId: "marker",
                            position: event.latLng,
                            map: addMap,
                            id: "1",
                        });
                        return;
                    }
                    console.log(event)
                    //marker.setPosition(event.latLng);
                    $that.addMapInfo.latLng.lat = event.latLng.lat;
                    $that.addMapInfo.latLng.lng = event.latLng.lng;

                    marker.updateGeometries([
                        {
                            "styleId": "marker",
                            "id": "1",
                            "position": event.latLng,
                        }
                    ])
                });
            }
        }
    });
})(window.vc);