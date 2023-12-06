(function (vc, vm) {

    vc.extends({
        data: {
            editLockMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                roomId: '',
                roomName: '',
                implBean: '',
                communityId: '',
                lockMachineParamDtoList: [],
                lockMachineFactorySpecList: [],
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLockMachine', 'openEditLockMachineModal', function (_params) {
                $that.refreshEditLockMachineInfo();
                $('#editLockMachineModel').modal('show');
                vc.copyObject(_params, $that.editLockMachineInfo);
                $that.editLockMachineInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.editLockMachineInfo.implBean) {
                    return ;
                }
                $that.lockMachineFactoryList.map(lockMachineFactory => {
                    if (lockMachineFactory.factoryId === $that.editLockMachineInfo.implBean) {
                        $that.editLockMachineInfo.lockMachineFactorySpecList = lockMachineFactory.lockMachineFactorySpecList;
                    }
                });
                if ($that.editLockMachineInfo.lockMachineFactorySpecList) {
                    let lockMachineParamDtoList = $that.editLockMachineInfo.lockMachineParamDtoList;
                    let lockMachineFactorySpecList = $that.editLockMachineInfo.lockMachineFactorySpecList;
                    for (let i = 0; i < lockMachineFactorySpecList.length; i++) {
                        for (let j = 0; j < lockMachineParamDtoList.length; j++) {
                            if (lockMachineFactorySpecList[i].specCd === lockMachineParamDtoList[j].specCd) {
                                lockMachineFactorySpecList[i].value = lockMachineParamDtoList[j].value;
                            }
                        }
                    }
                }
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
                    'editLockMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "门锁ID不能超过30"
                        },
                    ],
                    'editLockMachineInfo.machineName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "设备名称不能超过200"
                        },
                    ],
                    'editLockMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "设备编号不能超过64"
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
                    'editLockMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
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
                    implBean: '',
                    communityId: '',
                    lockMachineParamDtoList: [],
                    lockMachineFactorySpecList: [],
                }
            },
            _editLockMachineSelectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'editLockMachine'
                })
            },
            _editSelectLockMachineFactory: function () {
                $that.editLockMachineInfo.lockMachineFactorySpecList = [];
                if (!$that.editLockMachineInfo.implBean) {
                    vc.toast('未选择门锁厂家');
                    return ;
                }
                $that.lockMachineFactoryList.map(lockMachineFactory => {
                    if (lockMachineFactory.factoryId === $that.editLockMachineInfo.implBean) {
                        $that.editLockMachineInfo.lockMachineFactorySpecList = lockMachineFactory.lockMachineFactorySpecList;
                    }
                })
            }
        }
    });
})(window.vc, window.$that);
