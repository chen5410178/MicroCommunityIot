(function (vc, vm) {

    vc.extends({
        data: {
            editLampMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                locationName: '',
                factoryId: '',
                communityId: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLampMachine', 'openEditLampMachineModal', function (_params) {
                $that.refreshEditLampMachineInfo();
                $('#editLampMachineModel').modal('show');
                vc.copyObject(_params, $that.editLampMachineInfo);
                $that.editLampMachineInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editLampMachineValidate: function () {
                return vc.validate.validate({
                    editLampMachineInfo: $that.editLampMachineInfo
                }, {
                    'editLampMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        }
                    ],
                    'editLampMachineInfo.machineName': [
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
                    'editLampMachineInfo.machineCode': [
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
                    'editLampMachineInfo.locationName': [
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
                    'editLampMachineInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家不能为空"
                        }
                    ],
                    'editLampMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        }
                    ],
                });
            },
            editLampMachine: function () {
                if (!$that.editLampMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lampMachine.updateLampMachine',
                    JSON.stringify($that.editLampMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editLampMachineModel').modal('hide');
                            vc.emit('lampMachineManage', 'listLampMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditLampMachineInfo: function () {
                $that.editLampMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    factoryId: '',
                    communityId: '',
                    state: '',
                }
            }
        }
    });

})(window.vc, window.$that);
