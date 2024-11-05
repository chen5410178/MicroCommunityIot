import { _playWsVideo,_getCurMachineInfo ,toScreenPage} from "/api/video/monitorVideoApi.js";

(function (vc) {
    vc.extends({
        data: {
            monitorViewInfo: {
                datas: [],
            },

        },
        _initMethod: function () {
            $that.monitorViewInfo.datas=[];
            for (let divIndex = 1; divIndex < 9; divIndex++) {
                $that.monitorViewInfo.datas.push({
                    id: "monitor-channel-" + divIndex,
                    active: '',
                    jessibuca: {},
                })
            }
            $that._changeSelectCameraView(1);
        },
        _initEvent: function () {
            vc.on('monitorView', "playCameraVide", function (_machineId) {
                let _data = $that._getActiveData();
                if (!_data) {
                    return;
                }
                _getCurMachineInfo(_machineId,_data);
                $that._changeNextCameraVidew();
            })
        },
        methods: {
            _changeNextCameraVidew:function(){
                let _datas = $that.monitorViewInfo.datas;
                for(let _index = 0;_index < _datas.length;_index++){
                    if( _datas[_index].active == "camera-view-select"){
                        if(_index >= 7){
                            $that._changeSelectCameraView(1);
                            break;
                        }
                        $that._changeSelectCameraView(_index+2);
                        break;
                    }
                }
            },
            _changeSelectCameraView: function (_index) {
                let _datas = $that.monitorViewInfo.datas;
                _datas.forEach(_d => {
                    _d.active = "";
                });
                $that.monitorViewInfo.datas[_index - 1].active = "camera-view-select";
            },
            _getActiveData: function () {
                let _datas = $that.monitorViewInfo.datas;
                let _data = {};
                _datas.forEach(_d => {
                    if (_d.active == "camera-view-select") {
                        _data = _d;
                    }
                });
                return _data;
            },
            toScreenPage:function(_count){
                toScreenPage(_count);
            }
        }
    });

})(window.vc);