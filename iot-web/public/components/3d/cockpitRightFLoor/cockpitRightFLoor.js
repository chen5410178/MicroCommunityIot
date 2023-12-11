/**
    入驻小区
**/
(function (vc) {

    vc.extends({
        data: {
            cockpitRightFLoorInfo: {
                floorId: '',
                layer: 0,
                curLayer: '',
                floorName: '',
                floorNum: '',
                floorArea: '',
                roomCount: '',
                freeRoomCount: ''
            }
        },
        _initMethod: function () {


        },
        _initEvent: function () {

            vc.on('cockpitRightFLoor', 'notify', function (_floor) {
                $that.cockpitRightFLoorInfo.floorId = _floor.objId
                $that._loadFloor();
            })


        },
        methods: {

            _changeLayer: function (_layer) {
                $that.cockpitRightFLoorInfo.curLayer = _layer;
                vc.emit('cockpitCore', 'loadRoomModal', { floorId: $that.cockpitRightFLoorInfo.floorId, layer: _layer });
            },
            _loadFloor: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        communityId: vc.getCurrentCommunity().communityId,
                        floorId: $that.cockpitRightFLoorInfo.floorId
                    }
                }
                vc.http.apiGet('/floor.queryFloors',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);

                        vc.copyObject(_json.data[0], $that.cockpitRightFLoorInfo)

                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    })

            }
        }
    });
})(window.vc);