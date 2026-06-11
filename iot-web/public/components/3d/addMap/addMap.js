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

                if ($that.addMapInfo.latLng.lat && $that.addMapInfo.latLng.lng) {
                    _lat = $that.addMapInfo.latLng.lat;
                    _lng = $that.addMapInfo.latLng.lng;
                }

                let addMap = new TMap.Map(document.getElementById("addMapContainer"), {
                    center: new TMap.LatLng(parseFloat(_lat), parseFloat(_lng)),
                    zoom: 13
                });
                $that.addMapInfo.addMap = addMap;
                document.getElementById("addMapContainer")._map = addMap;

                let marker;
                marker = new TMap.MultiMarker({
                    map: addMap,
                    id: "1",
                });
                marker.updateGeometries([
                    {
                        "styleId": "marker",
                        "id": "1",
                        "position": new TMap.LatLng(parseFloat(_lat), parseFloat(_lng)),
                    }
                ]);

                if (!$that.addMapInfo.latLng.lat || !$that.addMapInfo.latLng.lng) {
                    $that.addMapInfo.latLng.lat = _lat;
                    $that.addMapInfo.latLng.lng = _lng;
                }

                addMap.on('click', function (event) {
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
