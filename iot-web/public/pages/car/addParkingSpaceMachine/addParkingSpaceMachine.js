(function (vc) {
    vc.extends({
        data: {
            addParkingSpaceMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                machineIp: '',
                machineMac: '',
                implBean: '',
                factorys: [],
                parkingSpaces:[]
            }
        },
        _initMethod: function () {
            $that._listSpaceMachineFactorys();
        },
        _initEvent: function () {
            vc.on('addParkingSpaceMachine', 'chooseParkingSpace', function(_parkingSpace) {
                $that._deleteParkingSpace({
                    psId:_parkingSpace.psId,
                    psName:_parkingSpace.paNum + "-" + _parkingSpace.num
                });
                $that.addParkingSpaceMachineInfo.parkingSpaces.push({
                    psId:_parkingSpace.psId,
                    psName:_parkingSpace.paNum + "-" + _parkingSpace.num
                });
            });
        },
        methods: {
            addParkingSpaceMachineValidate() {
                return vc.validate.validate({
                    addParkingSpaceMachineInfo: $that.addParkingSpaceMachineInfo
                }, {
                    'addParkingSpaceMachineInfo.machineName': [
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
                    'addParkingSpaceMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编码不能为空"
                        },
                    ],
                    'addParkingSpaceMachineInfo.machineIp': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备IP不能为空"
                        },
                    ],
                    'addParkingSpaceMachineInfo.machineMac': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备mac不能为空"
                        },
                    ],
                    'addParkingSpaceMachineInfo.implBean': [
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
                });
            },
            _saveParkingSpaceMachine: function () {
                if (!$that.addParkingSpaceMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addParkingSpaceMachineInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/parkingSpaceMachine.saveParkingSpaceMachine',
                    JSON.stringify($that.addParkingSpaceMachineInfo), {
                    emulateJSON: true
                },
                    function (json, res) {
                        let _json = JSON.parse(json)
                        if (_json.code == 0) {
                            vc.goBack();
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        vc.toast(errInfo);
                    });
            },
            openSearchParkingSpaceModel() {
                vc.emit('searchParkingSpace', 'openSearchParkingSpaceModel', {});
            },
            _deleteParkingSpace:function(_space){
                console.log(_space)
                let _spaces = $that.addParkingSpaceMachineInfo.parkingSpaces;
                let _tmpSpaces = [];
                _spaces.forEach(item => {
                    if(_space.psId != item.psId){
                        _tmpSpaces.push(item);
                    }
                });
                $that.addParkingSpaceMachineInfo.parkingSpaces = _tmpSpaces;
            },
            _listSpaceMachineFactorys: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100
                    }
                };

                //发送get请求
                vc.http.apiGet('/spaceMachineFactory.listSpaceMachineFactory',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addParkingSpaceMachineInfo.factorys = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);