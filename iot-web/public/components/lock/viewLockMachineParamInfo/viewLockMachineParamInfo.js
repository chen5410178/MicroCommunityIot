/**
 门锁设备参数 组件
 **/
(function (vc) {

    vc.extends({
        data: {
            lockMachineParamDtoList: []
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('viewLockMachineParamInfo', 'openViewLockMachineParamInfoModal', function (_lockMachine) {
                $that.lockMachineParamDtoList = _lockMachine.lockMachineParamDtoList;
                $('#viewLockMachineParamInfoModel').modal('show');
            });
        },
        methods: {

        }
    });

})(window.vc);
