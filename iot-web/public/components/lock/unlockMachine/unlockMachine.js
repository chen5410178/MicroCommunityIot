(function (vc, vm) {

    vc.extends({
        data: {
            unlockMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                roomId: '',
                roomName: '',
                charge: '',
                implBean: '',
                communityId: '',
                heartbeatTime: '',
                openTypeCd: '',
                password: '',
                logAction: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('unlockMachine', 'openUnlockMachineModal', function (_params) {
                $that.refreshUnlockMachineInfo();
                $('#unlockMachineModel').modal('show');
                vc.copyObject(_params, $that.unlockMachineInfo);
            });
        },
        methods: {
            unlockMachineValidate: function () {
                return vc.validate.validate({
                    unlockMachineInfo: $that.unlockMachineInfo
                }, {
                    'unlockMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁不能为空"
                        },
                    ],
                    'unlockMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁不能为空"
                        },
                    ],
                    'unlockMachineInfo.logAction': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "日志动作不能为空"
                        },
                    ],
                    'unlockMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区不能为空"
                        },
                    ],
                    'unlockMachineInfo.openTypeCd': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "开锁方式不能为空"
                        },
                    ],
                });
            },
            unlockMachine: function () {
                $that.unlockMachineInfo.logAction = 'unlock';
                if (!$that.unlockMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lockMachine.unLockMachine',
                    JSON.stringify($that.unlockMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $('#unlockMachineModel').modal('hide');
                            vc.toast(_json.msg);
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshUnlockMachineInfo: function () {
                $that.unlockMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    roomId: '',
                    roomName: '',
                    charge: '',
                    implBean: '',
                    communityId: '',
                    heartbeatTime: '',
                    openTypeCd: '',
                    password: '',
                    logAction: '',
                }
            },
        }
    });
})(window.vc, window.$that);
