(function (vc) {

    vc.extends({
        data: {
            addTelMachineInfo: {
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
            vc.on('addTelMachine', 'openAddTelMachineModal', function () {
                $('#addTelMachineModel').modal('show');
            });
        },
        methods: {
            addTelMachineValidate() {
                return vc.validate.validate({
                    addTelMachineInfo: $that.addTelMachineInfo
                }, {
                    'addTelMachineInfo.machineCode': [
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
                    'addTelMachineInfo.machineName': [
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
                    'addTelMachineInfo.machineIp': [
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
                    'addTelMachineInfo.machinePort': [
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
                    'addTelMachineInfo.implBean': [
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
                    'addTelMachineInfo.successPrompt': [
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
                    'addTelMachineInfo.failPrompt': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "未接听提示语'不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "未接听提示语不能超过512"
                        },
                    ],
                });
            },
            saveTelMachineInfo: function () {
                if (!$that.addTelMachineValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addTelMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
  
                vc.http.apiPost(
                    '/telMachine.saveTelMachine',
                    JSON.stringify($that.addTelMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addTelMachineModel').modal('hide');
                            $that.clearAddTelMachineInfo();
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
            clearAddTelMachineInfo: function () {
                $that.addTelMachineInfo = {
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    machinePort: '',
                    implBean: '',
                    successPrompt: '',
                    failPrompt: '',

                };
            }
        }
    });

})(window.vc);
