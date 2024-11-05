import { _playWsVideo,_getCurMachineInfo } from "/api/video/monitorVideoApi.js";

(function (vc) {
    vc.extends({
        data: {
            monitorViewFourInfo: {
                datas: [],
            },

        },
        _initMethod: function () {
            $that.monitorViewFourInfo.datas = [];
            for (let divIndex = 1; divIndex < 5; divIndex++) {
                $that.monitorViewFourInfo.datas.push({
                    id: "monitor-channel-" + divIndex,
                    active: '',
                    jessibuca: {},
                })
            }
            $that._changeSelectCameraFourView(1);
        },
        _initEvent: function () {
            vc.on('monitorViewFour', "playCameraVide", function (_machineId) {
                let _data = $that._getActiveDataFour();
                if (!_data) {
                    return;
                }
                _getCurMachineInfo(_machineId,_data);
                $that._changeNextCameraFourVidew();
            })
        },
        methods: {
            _changeNextCameraFourVidew:function(){
                let _datas = $that.monitorViewFourInfo.datas;
                for(let _index = 0;_index < _datas.length;_index++){
                    if( _datas[_index].active == "camera-view-select"){
                        if(_index >= 3){
                            $that._changeSelectCameraFourView(1);
                            break;
                        }
                        $that._changeSelectCameraFourView(_index+2);
                        break;
                    }
                }
            },
            _changeSelectCameraFourView: function (_index) {
                let _datas = $that.monitorViewFourInfo.datas;
                _datas.forEach(_d => {
                    _d.active = "";
                });
                $that.monitorViewFourInfo.datas[_index - 1].active = "camera-view-select";
            },
            _getActiveDataFour: function () {
                let _datas = $that.monitorViewFourInfo.datas;
                let _data = {};
                _datas.forEach(_d => {
                    if (_d.active == "camera-view-select") {
                        _data = _d;
                    }
                });
                return _data;
            },
            
        }
    });

})(window.vc);