(function (vc, vm) {

    vc.extends({
        data: {
            editMonitorMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                locationName: '',
                maId: '',
                mmId: '',
                protocol: '',
                communityId: '',
                state: '',
                attrSpecList: [],
                manufactors: []
            },
            initProtocol: '',
            initMonitorMachineAttrsList: '',
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editMonitorMachine', 'openEditMonitorMachineModal', function (_params) {
                $that.initProtocol = _params.protocol;
                $that.initMonitorMachineAttrsList = _params.monitorMachineAttrsList;
                $that.refreshEditMonitorMachineInfo();
                vc.copyObject(_params, $that.editMonitorMachineInfo);
                $that._listEditMonitorManufactors();
                $('#editMonitorMachineModel').modal('show');
            });
        },
        methods: {
            editMonitorMachineValidate: function () {
                return vc.validate.validate({
                    editMonitorMachineInfo: $that.editMonitorMachineInfo
                }, {
                    'editMonitorMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        },
                    ],
                    'editMonitorMachineInfo.machineName': [
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
                    'editMonitorMachineInfo.machineCode': [
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
                    'editMonitorMachineInfo.locationName': [
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
                    'editMonitorMachineInfo.maId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "区域ID不能为空"
                        },
                    ],
                    'editMonitorMachineInfo.protocol': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "监控协议不能为空"
                        },
                    ],
                    'editMonitorMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editMonitorMachineInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                });
            },
            editMonitorMachine: function () {
                if (!$that.editMonitorMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/monitorMachine.updateMonitorMachine',
                    JSON.stringify($that.editMonitorMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editMonitorMachineModel').modal('hide');
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
            refreshEditMonitorMachineInfo: function () {
                $that.editMonitorMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    maId: '',
                    mmId: '',
                    protocol: '',
                    communityId: '',
                    state: '',
                    attrSpecList: [],
                    manufactors: []
                }
            },
            _listEditMonitorManufactors: function () {

                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorManufactor.listMonitorManufactor',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.editMonitorMachineInfo.manufactors = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc, window.$that);
