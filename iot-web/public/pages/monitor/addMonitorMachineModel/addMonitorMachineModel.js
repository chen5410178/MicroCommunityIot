(function (vc) {

    vc.extends({
        data: {
            addMonitorMachineModelInfo: {
                mmmId: '',
                machineIdList: [],
                modelId: '',
                communityId: '',
            },
            monitorMachineNameOnlyView: [],
            monitorModelList: [],
            monitorAreaList: []
        },
        _initMethod: function () {
            $that.listMonitorModels();
            $that.listMonitorAreas();
        },
        _initEvent: function () {
            vc.on('', 'listMonitorMachineData', function (_data) {
                _data.map(machine =>{
                    $that.addMonitorMachineModelInfo.machineIdList.push(machine.machineId);
                    $that.monitorMachineNameOnlyView += machine.machineName + '\r\n';
                })
            });
        },
        methods: {
            addMonitorMachineModelValidate() {
                return vc.validate.validate({
                    addMonitorMachineModelInfo: $that.addMonitorMachineModelInfo
                }, {
                    'addMonitorMachineModelInfo.modelId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控模型不能为空"
                        },
                    ],
                    'addMonitorMachineModelInfo.machineIdList': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控设备不能为空"
                        },
                    ],
                    'addMonitorMachineModelInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                });
            },
            saveMonitorMachineModelInfo: function () {

                $that.addMonitorMachineModelInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addMonitorMachineModelValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/monitorMachineModel.saveMonitorMachineModel',
                    JSON.stringify($that.addMonitorMachineModelInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that.clearaddMonitorMachineModelInfo();
                            vc.getBack();
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearaddMonitorMachineModelInfo: function () {
                $that.addMonitorMachineModelInfo = {
                    mmmId: '',
                    machineIdList: [],
                    modelId: '',
                    communityId: '',
                };
            },
            selectMonitorMachine: function () {
                $that.addMonitorMachineModelInfo.machineIdList = [];
                $that.monitorMachineNameOnlyView = '';
                vc.emit('chooseMonitorMachine', 'openChooseMonitorMachineModel', '');
            },
            listMonitorModels: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorModel.listMonitorModel',
                    param,
                    function (json, res) {
                        var _monitorModelManageInfo = JSON.parse(json);
                        $that.monitorModelList = _monitorModelManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            listMonitorAreas: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorArea.listMonitorArea',
                    param,
                    function (json, res) {
                        var _monitorAreaManageInfo = JSON.parse(json);
                        $that.monitorAreaList = _monitorAreaManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
