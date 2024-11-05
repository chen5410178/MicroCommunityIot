(function (vc) {
    vc.extends({
        data: {
            monitorVideoInfo: {
                datas: [],
                screenCount: 8
            },

        },
        _initMethod: function () {
            let _count = vc.getParam('screenCount');
            if (_count) {
                $that.monitorVideoInfo.screenCount = _count;
            }
        },
        _initEvent: function () {
            vc.on('monitorVideo', 'selectVideo', function (_param) {
                let _count = $that.monitorVideoInfo.screenCount;
                if(_count == 8){
                    vc.emit("monitorView", "playCameraVide",_param.machineId);
                }else if(_count == 1){
                    vc.emit("monitorViewOne", "playCameraVide",_param.machineId);
                }else if(_count == 4){
                    vc.emit("monitorViewFour", "playCameraVide",_param.machineId);
                }
                else if(_count == 9){
                    vc.emit("monitorViewNine", "playCameraVide",_param.machineId);
                }
            });
        },
        methods: {

        }
    });

})(window.vc);