(function (vc, vm) {

    vc.extends({
        data: {
            editAttendanceMachineInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                machineIp: '',
                heartbeatTime: '',
                implBean: '',
                classesId: '',
                classesName: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editAttendanceMachine', 'openEditAttendanceMachineModal', function (_params) {
                $that.refreshEditAttendanceMachineInfo();
                $('#editAttendanceMachineModel').modal('show');
                vc.copyObject(_params, $that.editAttendanceMachineInfo);
                $that.editAttendanceMachineInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editAttendanceMachineValidate: function () {
                return vc.validate.validate({
                    editAttendanceMachineInfo: $that.editAttendanceMachineInfo
                }, {
                    'editAttendanceMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编码不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "设备编码不能超过30"
                        },
                    ],
                    'editAttendanceMachineInfo.machineName': [
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
                  
                    'editAttendanceMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
                        },
                    ],
                    'editAttendanceMachineInfo.classesId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "班组ID不能为空"
                        },
                    ],
                    'editAttendanceMachineInfo.classesName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "班组名称不能为空"
                        },
                    ],
                    'editAttendanceMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editAttendanceMachine: function () {
                if (!$that.editAttendanceMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/attendance.updateAttendanceMachine',
                    JSON.stringify($that.editAttendanceMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editAttendanceMachineModel').modal('hide');
                            vc.emit('attendanceMachine', 'listAttendanceMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditAttendanceMachineInfo: function () {
                $that.editAttendanceMachineInfo = {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    heartbeatTime: '',
                    implBean: '',
                    classesId: '',
                    classesName: '',

                }
            }
        }
    });

})(window.vc, window.$that);
