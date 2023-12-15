/**
 入驻小区
 **/
import { parkingWebSocket } from "/api/websocket/parkingWebSocket.js";

(function (vc) {
    vc.extends({
        data: {
            parkingAreaTotalControlInfo: {
                _currentTab: 'parkingAreaTotalControlVideo',
                paId: '',
                inMachineId: '',
                outMachineId: ''
            }
        },
        _initMethod: function () {
            $that.parkingAreaTotalControlInfo.paId = vc.getParam('paId');

            $that._initParkingAreaWs();
            vc.emit('parkingAreaTotalControlVideo', 'notify', {
                paId: $that.parkingAreaTotalControlInfo.paId
            });
        },
        _initEvent: function () {
            vc.on('parkingAreaTotalControl', 'notify', function (_param) {
                vc.copyObject(_param, $that.parkingAreaTotalControlInfo);
            })
        },
        methods: {
            changeTab: function (_tab) {
                $that.parkingAreaTotalControlInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    paId: $that.parkingAreaTotalControlInfo.paId
                })
            },
            _initParkingAreaWs: function () {
                let _url = "/ws/parkingArea/" + $that.parkingAreaTotalControlInfo.paId;
                new parkingWebSocket(_url, function (_data) {
                    vc.emit('parkingAreaTotalControlVideo', 'notify', _data);
                });
            }
        }
    });
})(window.vc);