(function (vc, vm) {

    vc.extends({
        data: {
            editTelMachineInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                machineIp: '',
                machinePort: '',
                implBean: '',
                successPrompt: '',
                failPrompt: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editTelMachine', 'openEditTelMachineModal', function (_params) {
                $that.refreshEditTelMachineInfo();
                $('#editTelMachineModel').modal('show');
                vc.copyObject(_params, $that.editTelMachineInfo);
                $that.editTelMachineInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editTelMachineValidate: function () {
                return vc.validate.validate({
                    editTelMachineInfo: $that.editTelMachineInfo
                }, {
                    'editTelMachineInfo.machineCode': [
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
                    'editTelMachineInfo.machineName': [
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
                    'editTelMachineInfo.machineIp': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备IP不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "设备IP不能超过64"
                        },
                    ],
                    'editTelMachineInfo.machinePort': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备端口不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "设备端口不能超过64"
                        },
                    ],
                    'editTelMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "门禁厂家不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "门禁厂家不能超过30"
                        },
                    ],
                    'editTelMachineInfo.successPrompt': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "接听提示语不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "接听提示语不能超过512"
                        },
                    ],
                    'editTelMachineInfo.failPrompt': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "未接听提示语不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "未接听提示语不能超过512"
                        },
                    ],
                    'editTelMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editTelMachine: function () {
                if (!$that.editTelMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/telMachine.updateTelMachine',
                    JSON.stringify($that.editTelMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editTelMachineModel').modal('hide');
                            vc.emit('telMachine', 'listTelMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditTelMachineInfo: function () {
                $that.editTelMachineInfo = {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    machinePort: '',
                    implBean: '',
                    successPrompt: '',
                    failPrompt: '',

                }
            }
        }
    });

})(window.vc, window.$that);
