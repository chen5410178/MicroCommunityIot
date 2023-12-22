(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMonitorMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                locationName: '',
                maId: '',
                protocol: '',
                communityId: '',
                state: '',
                attrSpecList: []
            },
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMonitorMachine', 'openAddMonitorMachineModal', function () {
                $('#addMonitorMachineModel').modal('show');
            });
        },
        methods: {
            addMonitorMachineValidate() {
                return vc.validate.validate({
                    addMonitorMachineInfo: $that.addMonitorMachineInfo
                }, {
                    'addMonitorMachineInfo.machineName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "监控名称不能超过200"
                        },
                    ],
                    'addMonitorMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "监控编号不能超过30"
                        },
                    ],
                    'addMonitorMachineInfo.locationName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控位置不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "监控位置不能超过64"
                        },
                    ],
                    'addMonitorMachineInfo.maId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控区域不能为空"
                        },
                    ],
                    'addMonitorMachineInfo.protocol': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控协议不能为空"
                        },
                    ],
                    'addMonitorMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            saveMonitorMachineInfo: function () {
                $that.addMonitorMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addMonitorMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
              
                vc.http.apiPost(
                    '/monitorMachine.saveMonitorMachine',
                    JSON.stringify($that.addMonitorMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addMonitorMachineModel').modal('hide');
                            $that.clearAddMonitorMachineInfo();
                            vc.emit('monitorMachineManage', 'listMonitorMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddMonitorMachineInfo: function () {
                $that.addMonitorMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    maId: '',
                    protocol: '',
                    communityId: '',
                    state: '',
                    attrSpecList: []
                };
            },
            listAttrSpec: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        tableName: 'machine_attr',
                        domain: 'MONITOR.' + $that.addMonitorMachineInfo.protocol,
                    }
                }
                vc.http.apiGet('/attrSpec.listAttrSpec',
                    param,
                    function (json, res) {
                        var _attrSpecInfo = JSON.parse(json);
                        $that.addMonitorMachineInfo.attrSpecList = _attrSpecInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }
        }
    });
})(window.vc);
