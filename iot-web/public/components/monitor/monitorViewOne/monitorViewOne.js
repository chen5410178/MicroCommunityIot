import { _playWsVideo,_getCurMachineInfo } from "/api/video/monitorVideoApi.js";

(function (vc) {
    vc.extends({
        data: {
            monitorViewOneInfo: {
                datas: [],
            },

        },
        _initMethod: function () {
            $that.monitorViewOneInfo.datas=[];
            for (let divIndex = 1; divIndex < 2; divIndex++) {
                $that.monitorViewOneInfo.datas.push({
                    id: "monitor-channel-" + divIndex,
                    active: '',
                    jessibuca: {},
                })
            }
            $that._changeSelectCameraOneView(1);
        },
        _initEvent: function () {
            vc.on('monitorViewOne', "playCameraVide", function (_machineId) {
                let _data = $that._getActiveDataOne();
                if (!_data) {
                    return;
                }
                _getCurMachineInfo(_machineId,_data);
            })
        },
        methods: {
            _changeSelectCameraOneView: function (_index) {
                let _datas = $that.monitorViewOneInfo.datas;
                _datas.forEach(_d => {
                    _d.active = "";
                });
                $that.monitorViewOneInfo.datas[_index - 1].active = "camera-view-select";
            },
            _getActiveDataOne: function () {
                let _datas = $that.monitorViewOneInfo.datas;
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