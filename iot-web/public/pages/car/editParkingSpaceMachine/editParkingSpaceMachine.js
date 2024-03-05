(function (vc) {
    vc.extends({
        data: {
            editParkingSpaceMachineInfo: {
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
            $that.editParkingSpaceMachineInfo.machineId = vc.getParam('machineId');
            $that._listParkingSpaceMachines();
            $that._listSpaceMachineFactorys();
        },
        _initEvent: function () {
            vc.on('editParkingSpaceMachine', 'chooseParkingSpace', function(_parkingSpace) {
                $that._deleteParkingSpace({
                    psId:_parkingSpace.psId,
                    psName:_parkingSpace.paNum + "-" + _parkingSpace.num
                });
                $that.editParkingSpaceMachineInfo.parkingSpaces.push({
                    psId:_parkingSpace.psId,
                    psName:_parkingSpace.paNum + "-" + _parkingSpace.num
                });
            });
        },
        methods: {
            editParkingSpaceMachineValidate() {
                return vc.validate.validate({
                    editParkingSpaceMachineInfo: $that.editParkingSpaceMachineInfo
                }, {
                    'editParkingSpaceMachineInfo.machineName': [
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
                    'editParkingSpaceMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编码不能为空"
                        },
                    ],
                    'editParkingSpaceMachineInfo.machineIp': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备IP不能为空"
                        },
                    ],
                    'editParkingSpaceMachineInfo.machineMac': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备mac不能为空"
                        },
                    ],
                    'editParkingSpaceMachineInfo.implBean': [
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
            _updateParkingSpaceMachine: function () {
                if (!$that.editParkingSpaceMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.editParkingSpaceMachineInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/parkingSpaceMachine.updateParkingSpaceMachine',
                    JSON.stringify($that.editParkingSpaceMachineInfo), {
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
                let _spaces = $that.editParkingSpaceMachineInfo.parkingSpaces;
                let _tmpSpaces = [];
                _spaces.forEach(item => {
                    if(_space.psId != item.psId){
                        _tmpSpaces.push(item);
                    }
                });
                $that.editParkingSpaceMachineInfo.parkingSpaces = _tmpSpaces;
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
                        $that.editParkingSpaceMachineInfo.factorys = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listParkingSpaceMachines: function (_page, _rows) {

                let param = {
                    params: {
                        page:1,
                        row:1,
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.editParkingSpaceMachineInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/parkingSpaceMachine.listParkingSpaceMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0],$that.editParkingSpaceMachineInfo);
                        $that.editParkingSpaceMachineInfo.parkingSpaces = _json.data[0].parkingSpaces;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);