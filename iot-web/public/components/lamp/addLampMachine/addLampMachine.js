(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLampMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                locationName: '',
                factoryId: '',
                communityId: '',
            },
        },
        _initMethod: function () {
            
        },
        _initEvent: function () {
            vc.on('addLampMachine', 'openAddLampMachineModal', function () {
                $('#addLampMachineModel').modal('show');
            });
        },
        methods: {
            addLampMachineValidate() {
                return vc.validate.validate({
                    addLampMachineInfo: $that.addLampMachineInfo
                }, {
                    'addLampMachineInfo.machineName': [
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
                    'addLampMachineInfo.machineCode': [
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
                    'addLampMachineInfo.locationName': [
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
                    'addLampMachineInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家不能为空"
                        }
                    ],
                    'addLampMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        }
                    ],
                });
            },
            saveLampMachineInfo: function () {
                $that.addLampMachineInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addLampMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addLampMachineInfo);
                    $('#addLampMachineModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/lampMachine.saveLampMachine',
                    JSON.stringify($that.addLampMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLampMachineModel').modal('hide');
                            $that.clearAddLampMachineInfo();
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
            clearAddLampMachineInfo: function () {
                $that.addLampMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    factoryId: '',
                    communityId: '',
                };
            },
        }
    });

})(window.vc);
