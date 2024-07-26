(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLiftMachineInfo: {
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
                monitors:[],
                monitorId:'',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLiftMachine', 'openAddLiftMachineModal', function () {
                $that._listAddMonitors();
                $('#addLiftMachineModel').modal('show');
            });
        },
        methods: {
            addLiftMachineValidate() {
                return vc.validate.validate({
                    addLiftMachineInfo: $that.addLiftMachineInfo
                }, {
                    'addLiftMachineInfo.machineName': [
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
                    'addLiftMachineInfo.machineCode': [
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
                    'addLiftMachineInfo.brand': [
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
                    'addLiftMachineInfo.locationName': [
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
                    'addLiftMachineInfo.registrationUnit': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "登记单位不能超过64"
                        },
                    ],
                    'addLiftMachineInfo.useUnit': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "使用单位不能超过64"
                        },
                    ],
                    'addLiftMachineInfo.maintenanceUnit': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "维保单位不能超过64"
                        },
                    ],
                    'addLiftMachineInfo.rescueLink': [
                        {
                            limit: "maxLength",
                            param: "11",
                            errInfo: "救援电话不能超过11"
                        },
                    ],
                    'addLiftMachineInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家不能为空"
                        }
                    ],
                    'addLiftMachineInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        }
                    ],
                });
            },
            saveLiftMachineInfo: function () {
                $that.addLiftMachineInfo.communityId = vc.getCurrentCommunity().communityId;

                if (!$that.addLiftMachineValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

   
                vc.http.apiPost(
                    '/liftMachine.saveLiftMachine',
                    JSON.stringify($that.addLiftMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addLiftMachineModel').modal('hide');
                            $that.clearAddLiftMachineInfo();
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
            clearAddLiftMachineInfo: function () {
                $that.addLiftMachineInfo = {
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
                    monitors:[],
                    monitorId:'',
                };
            },
            _listAddMonitors: function (_page, _rows) {
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
                        $that.addLiftMachineInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);
