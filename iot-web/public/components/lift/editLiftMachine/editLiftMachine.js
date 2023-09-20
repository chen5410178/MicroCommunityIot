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
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLiftMachine', 'openEditLiftMachineModal', function (_params) {
                vc.component.refreshEditLiftMachineInfo();
                $('#editLiftMachineModel').modal('show');
                vc.copyObject(_params, vc.component.editLiftMachineInfo);
                vc.component.editLiftMachineInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editLiftMachineValidate: function () {
                return vc.validate.validate({
                    editLiftMachineInfo: vc.component.editLiftMachineInfo
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
                if (!vc.component.editLiftMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'liftMachine.updateLiftMachine',
                    JSON.stringify(vc.component.editLiftMachineInfo),
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
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);
                    });
            },
            refreshEditLiftMachineInfo: function () {
                vc.component.editLiftMachineInfo = {
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
                }
            }
        }
    });

})(window.vc, window.vc.component);
