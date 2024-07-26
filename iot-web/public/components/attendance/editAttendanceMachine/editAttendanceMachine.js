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
                hms:[],
                monitors:[],
                monitorId:'',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editAttendanceMachine', 'openEditAttendanceMachineModal', function (_params) {
                $that.refreshEditAttendanceMachineInfo();
                $('#editAttendanceMachineModel').modal('show');
                vc.copyObject(_params, $that.editAttendanceMachineInfo);
                $that._listEditHms();
                $that._listEditMonitors();
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
                    hms:[],
                    monitors:[],
                    monitorId:'',
                }
            },
            _listEditHms: function(_page, _rows) {

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
                        $that.editAttendanceMachineInfo.hms = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listEditMonitors: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.editAttendanceMachineInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc, window.$that);
