(function (vc) {

    vc.extends({
        data: {
            addAttendanceMachineInfo: {
                wtId: '',
                machineCode: '',
                machineName: '',
                machineIp: '',
                heartbeatTime: '',
                implBean: '',
                classesId: '',
                classesName: '',
                hms: []
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addAttendanceMachine', 'openAddAttendanceMachineModal', function () {
                $that._listAddHms();
                $('#addAttendanceMachineModel').modal('show');
            });
        },
        methods: {
            addAttendanceMachineValidate() {
                return vc.validate.validate({
                    addAttendanceMachineInfo: $that.addAttendanceMachineInfo
                }, {
                    'addAttendanceMachineInfo.machineCode': [
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
                    'addAttendanceMachineInfo.machineName': [
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
                    'addAttendanceMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
                        },
                    ],
                    'addAttendanceMachineInfo.classesId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "班组ID不能为空"
                        },
                    ],
                    'addAttendanceMachineInfo.classesName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "班组名称不能为空"
                        },
                    ],
                });
            },
            saveAttendanceMachineInfo: function () {
                if (!$that.addAttendanceMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addAttendanceMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/attendance.saveAttendanceMachine',
                    JSON.stringify($that.addAttendanceMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addAttendanceMachineModel').modal('hide');
                            $that.clearAddAttendanceMachineInfo();
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
            clearAddAttendanceMachineInfo: function () {
                $that.addAttendanceMachineInfo = {
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    heartbeatTime: '',
                    implBean: '',
                    classesId: '',
                    classesName: '',
                    hms:[]
                };
            },
            _listAddHms: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        hmType: '4004'
                    }
                };

                //发送get请求
                vc.http.apiGet('/hm.listHardwareManufacturer',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.addAttendanceMachineInfo.hms = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);
