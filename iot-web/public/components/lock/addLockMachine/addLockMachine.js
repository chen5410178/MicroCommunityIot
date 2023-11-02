(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLockMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                roomId: '',
                roomName: '',
                implBean: '',
                communityId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLockMachine', 'openAddLockMachineModal', function () {
                $('#addLockMachineModel').modal('show');
            });
            vc.on('addLockMachine', 'selectRoom', function(param) {
                vc.copyObject(param, $that.addLockMachineInfo);
            });
        },
        methods: {
            addLockMachineValidate() {
                return vc.validate.validate({
                    addLockMachineInfo: $that.addLockMachineInfo
                }, {
                    'addLockMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
                        },
                    ],
                    'addLockMachineInfo.machineName': [
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
                    'addLockMachineInfo.machineCode': [
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
                    'addLockMachineInfo.roomId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "房屋不能为空"
                        },
                    ],
                    'addLockMachineInfo.roomName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "房屋不能为空"
                        },
                    ],
                    'addLockMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            saveLockMachineInfo: function () {
                $that.addLockMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addLockMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addLockMachineInfo);
                    $('#addLockMachineModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/lockMachine.saveLockMachine',
                    JSON.stringify($that.addLockMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLockMachineModel').modal('hide');
                            $that.clearAddLockMachineInfo();
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
            clearAddLockMachineInfo: function () {
                $that.addLockMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    roomId: '',
                    roomName: '',
                    implBean: '',
                    communityId: '',
                };
            },
            _selectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'addLockMachine'
                })
            },
        }
    });
})(window.vc);
