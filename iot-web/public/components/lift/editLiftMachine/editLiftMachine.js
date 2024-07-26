(function (vc, vm) {

    vc.extends({
        data: {
            editLiftMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                brand: '',
                locationName: '',
                registrationUnit: '',
                useUnit: '',
                maintenanceUnit: '',
                rescueLink: '',
                factoryId: '',
                communityId: '',
                monitors: [],
                monitorId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLiftMachine', 'openEditLiftMachineModal', function (_params) {
                $that.refreshEditLiftMachineInfo();
                $that._listEditMonitors();
                $('#editLiftMachineModel').modal('show');
                vc.copyObject(_params, $that.editLiftMachineInfo);
                $that.editLiftMachineInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editLiftMachineValidate: function () {
                return vc.validate.validate({
                    editLiftMachineInfo: $that.editLiftMachineInfo
                }, {
                    'editLiftMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        }
                    ],
                    'editLiftMachineInfo.machineName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "名称不能超过200"
                        },
                    ],
                    'editLiftMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "设备编号不能超过30"
                        },
                    ],
                    'editLiftMachineInfo.brand': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "品牌不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "品牌不能超过64"
                        },
                    ],
                    'editLiftMachineInfo.locationName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "位置不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "位置不能超过64"
                        },
                    ],
                    'editLiftMachineInfo.registrationUnit': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "登记单位不能超过64"
                        },
                    ],
                    'editLiftMachineInfo.useUnit': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "使用单位不能超过64"
                        },
                    ],
                    'editLiftMachineInfo.maintenanceUnit': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "维保单位不能超过64"
                        },
                    ],
                    'editLiftMachineInfo.rescueLink': [
                        {
                            limit: "maxLength",
                            param: "11",
                            errInfo: "救援电话不能超过11"
                        },
                    ],
                    'editLiftMachineInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家不能为空"
                        },
                    ],
                    'editLiftMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            editLiftMachine: function () {
                if (!$that.editLiftMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/liftMachine.updateLiftMachine',
                    JSON.stringify($that.editLiftMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editLiftMachineModel').modal('hide');
                            vc.emit('liftMachineManage', 'listLiftMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditLiftMachineInfo: function () {
                $that.editLiftMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    brand: '',
                    locationName: '',
                    registrationUnit: '',
                    useUnit: '',
                    maintenanceUnit: '',
                    rescueLink: '',
                    factoryId: '',
                    communityId: '',
                    monitors: [],
                    monitorId: '',
                }
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
                        $that.editLiftMachineInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc, window.$that);
