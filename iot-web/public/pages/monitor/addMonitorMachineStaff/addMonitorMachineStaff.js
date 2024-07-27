(function (vc) {
    vc.extends({
        data: {
            addMonitorMachineStaffInfo: {
                csId: '',
                machineId: '',
                staffId: '',
                staffName: '',
                personType: '2002'
            }
        },
        _initMethod: function () {
            $that.addMonitorMachineStaffInfo.machineId = vc.getParam('machineId');
        },
        _initEvent: function () {
          
        },
        methods: {
            addMonitorMachineStaffValidate() {
                return vc.validate.validate({
                    addMonitorMachineStaffInfo: $that.addMonitorMachineStaffInfo
                }, {
                    'addMonitorMachineStaffInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        }
                    ],
                    'addMonitorMachineStaffInfo.staffId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "员工名称不能为空"
                        }
                    ]
                });
            },
            saveMonitorMachineStaffInfo: function () {
                if (!$that.addMonitorMachineStaffValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addMonitorMachineStaffInfo.communityId = vc.getCurrentCommunity().communityId;
                $that.addMonitorMachineStaffInfo.personId = $that.addMonitorMachineStaffInfo.staffId;
                $that.addMonitorMachineStaffInfo.personName = $that.addMonitorMachineStaffInfo.staffName;
                vc.http.apiPost(
                    '/monitor.saveMonitorMachinePerson',
                    JSON.stringify($that.addMonitorMachineStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            vc.goBack();
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(errInfo);
                    });
            },
            openChooseStaff: function () {
                vc.emit('searchStaff', 'openSearchStaffModel', $that.addMonitorMachineStaffInfo);
            },
            _goBack: function () {
                vc.goBack();
            }
        }
    });
})(window.vc);
