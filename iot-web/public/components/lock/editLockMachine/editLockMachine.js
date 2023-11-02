(function (vc, vm) {

    vc.extends({
        data: {
            editLockMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                roomId: '',
                roomName: '',
                charge: '',
                implBean: '',
                communityId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLockMachine', 'openEditLockMachineModal', function (_params) {
                $that.refreshEditLockMachineInfo();
                $('#editLockMachineModel').modal('show');
                vc.copyObject(_params, $that.editLockMachineInfo);
            });
            vc.on('editLockMachine', 'selectRoom', function(param) {
                vc.copyObject(param, $that.editLockMachineInfo);
            });
        },
        methods: {
            editLockMachineValidate: function () {
                return vc.validate.validate({
                    editLockMachineInfo: $that.editLockMachineInfo
                }, {
                    'editLockMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
                        },
                    ],
                    'editLockMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁ID不能为空"
                        },
                    ],
                    'editLockMachineInfo.machineName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "门锁名称不能超过200"
                        },
                    ],
                    'editLockMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门锁编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "门锁编号不能超过64"
                        },
                    ],
                    'editLockMachineInfo.roomId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "房屋不能为空"
                        },
                    ],
                    'editLockMachineInfo.roomName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "房屋不能为空"
                        },
                    ],
                    'editLockMachineInfo.charge': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "剩余电量不能为空"
                        },
                    ],
                    'editLockMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            editLockMachine: function () {
                if (!$that.editLockMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lockMachine.updateLockMachine',
                    JSON.stringify($that.editLockMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editLockMachineModel').modal('hide');
                            vc.emit('lockMachineManage', 'listLockMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditLockMachineInfo: function () {
                $that.editLockMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    roomId: '',
                    roomName: '',
                    charge: '',
                    implBean: '',
                    communityId: '',
                }
            },
            _selectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'editLockMachine'
                })
            },
        }
    });
})(window.vc, window.$that);
