(function (vc) {

    vc.extends({
        data: {
            addLampMachineOpenCloseInfo: {
                lmoId: '',
                machineIdList: [],
                type: '',
                hours: '',
                min: '',
            },
            typeList: [],
            lampNameOnlyView: '',
        },
        _initMethod: function () {
            vc.getDict('lamp_machine_operation', 'type', function (_data) {
                $that.typeList = _data;
            })
        },
        _initEvent: function () {
            vc.on('addLampMachineOpenClose', 'openAddLampMachineOpenCloseModal', function () {
                $('#addLampMachineOpenCloseModel').modal('show');
            });
            vc.on('', 'listLampMachineData', function (_data) {
                _data.map(lampMachine =>{
                    $that.addLampMachineOpenCloseInfo.machineIdList.push(lampMachine.machineId);
                    $that.lampNameOnlyView += lampMachine.machineName + '\r\n';
                })
                //$that.addLampMachineOpenCloseInfo.machineIdList = _data;
            })
        },
        methods: {
            addLampMachineOpenCloseValidate() {
                return vc.validate.validate({
                    addLampMachineOpenCloseInfo: $that.addLampMachineOpenCloseInfo
                }, {
                    'addLampMachineOpenCloseInfo.type': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "类型不能为空"
                        }
                    ],
                    'addLampMachineOpenCloseInfo.hours': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小时不能为空"
                        },
                        {
                            limit: "max",
                            param: "23",
                            errInfo: "小时不能超过23"
                        },
                        {
                            limit: "min",
                            param: "0",
                            errInfo: "小时不能低于0"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "小时必须为整数"
                        },
                    ],
                    'addLampMachineOpenCloseInfo.min': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "分钟不能为空"
                        },
                        {
                            limit: "max",
                            param: "59",
                            errInfo: "分钟不能超过59"
                        },
                        {
                            limit: "min",
                            param: "0",
                            errInfo: "分钟不能低于0"
                        },
                        {
                            limit: "num",
                            param: "",
                            errInfo: "分钟必须为整数"
                        },
                    ],
                    'addLampMachineOpenCloseInfo.machineIdList': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "路灯设备不能为空"
                        }
                    ],
                });
            },
            saveLampMachineOpenCloseInfo: function () {
                if (!$that.addLampMachineOpenCloseValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.addLampMachineOpenCloseInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/lampMachineOpenClose.saveLampMachineOpenClose',
                    JSON.stringify($that.addLampMachineOpenCloseInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $that.clearAddLampMachineOpenCloseInfo();
                            vc.goBack();
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLampMachineOpenCloseInfo: function () {
                $that.addLampMachineOpenCloseInfo = {
                    lmoId: '',
                    machineId: '',
                    type: '',
                };
            },
            selectLampMachine: function () {
                $that.lampNameOnlyView = '';
                $that.addLampMachineOpenCloseInfo.machineIdList = [];
                vc.emit('chooseLampMachine', 'openChooseLampMachineModel', {});
            },
        }
    });

})(window.vc);
