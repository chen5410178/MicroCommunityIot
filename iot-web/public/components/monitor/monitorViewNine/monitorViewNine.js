import { _playWsVideo,_getCurMachineInfo } from "/api/video/monitorVideoApi.js";

(function (vc) {
    vc.extends({
        data: {
            monitorViewNineInfo: {
                datas: [],
            },

        },
        _initMethod: function () {
            $that.monitorViewNineInfo.datas=[];
            for (let divIndex = 1; divIndex < 10; divIndex++) {
                $that.monitorViewNineInfo.datas.push({
                    id: "monitor-channel-" + divIndex,
                    active: '',
                    jessibuca: {},
                })
            }
            $that._changeSelectCameraNineView(1);
        },
        _initEvent: function () {
            vc.on('monitorViewNine', "playCameraVide", function (_machineId) {
                let _data = $that._getActiveDataNine();
                if (!_data) {
                    return;
                }
                _getCurMachineInfo(_machineId,_data);
                $that._changeNextCameraNineVidew();
            })
        },
        methods: {
            _changeNextCameraNineVidew:function(){
                let _datas = $that.monitorViewNineInfo.datas;
                for(let _index = 0;_index < _datas.length;_index++){
                    if( _datas[_index].active == "camera-view-select"){
                        if(_index >= 8){
                            $that._changeSelectCameraNineView(1);
                            break;
                        }
                        $that._changeSelectCameraNineView(_index+2);
                        break;
                    }
                }
            },
            _changeSelectCameraNineView: function (_index) {
                let _datas = $that.monitorViewNineInfo.datas;
                _datas.forEach(_d => {
                    _d.active = "";
                });
                $that.monitorViewNineInfo.datas[_index - 1].active = "camera-view-select";
            },
            _getActiveDataNine: function () {
                let _datas = $that.monitorViewNineInfo.datas;
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