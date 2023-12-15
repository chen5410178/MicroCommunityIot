/**
 入驻小区
 **/
import { parkingWebSocket } from "/api/websocket/parkingWebSocket.js";
(function(vc) {
    vc.extends({
        data: {
            parkingAreaControlInfo: {
                _currentTab: 'parkingAreaControlCarInout',
                boxId: '',
                paId: '',
                inMachineId: '',
                outMachineId: '',
                ws:{}
            }
        },
        _initMethod: function() {
            $that.parkingAreaControlInfo.boxId = vc.getParam('boxId');
            $that.parkingAreaControlInfo.paId = vc.getParam('paId');

            $that._initParkingAreaWs();
            vc.emit('parkingAreaControlVideo', 'notify', {
                boxId: $that.parkingAreaControlInfo.boxId,
            });
        },
        _initEvent: function() {
            vc.on('parkingAreaControl', 'notify', function(_param) {
                vc.copyObject(_param, $that.parkingAreaControlInfo);
            });

 
        },
        methods: {
            changeTab: function(_tab) {
                $that.parkingAreaControlInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    boxId: $that.parkingAreaControlInfo.boxId,
                    paId: $that.parkingAreaControlInfo.paId
                })
            },
            _initParkingAreaWs: function() {
                let _url = "/ws/parkingBox/" + $that.parkingAreaControlInfo.boxId;
                 $that.parkingAreaControlInfo.ws = new parkingWebSocket(_url,function(_data){
                    vc.emit('parkingAreaControlCarInout', 'notify', {
                        data: _data,
                        parkingAreaControl: $that.parkingAreaControlInfo
                    });
                    vc.emit('parkingAreaControlFee', 'notify', _data);
                    vc.emit('parkingAreaControlTempCarAuthConfirm', 'notify', _data);
                 });
            },

        }
    });
})(window.vc);