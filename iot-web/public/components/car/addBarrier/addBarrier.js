(function (vc) {

    vc.extends({
        data: {
            addBarrierInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                machineIp: '',
                direction: '',
                implBean: '',
                boxId: '',
                hms: [],
                parkingBoxs:[]
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addBarrier', 'openAddBarrierModal', function () {
                $that._listAddHms();
                $that._listAddParkingBoxs();
                $('#addBarrierModel').modal('show');
            });
        },
        methods: {
            addBarrierValidate() {
                return vc.validate.validate({
                    addBarrierInfo: $that.addBarrierInfo
                }, {
                    'addBarrierInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编码不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "设备编码不能超过64"
                        },
                    ],
                    'addBarrierInfo.machineName': [
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
                    'addBarrierInfo.machineIp': [
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
                    'addBarrierInfo.direction': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备方向不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "设备方向不能超过12"
                        },
                    ],
                    'addBarrierInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "道闸厂家不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "道闸厂家不能超过30"
                        },
                    ],
                    'addBarrierInfo.boxId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "岗亭ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "岗亭ID不能超过30"
                        },
                    ],
                });
            },
            saveBarrierInfo: function () {
                if (!$that.addBarrierValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }
                $that.addBarrierInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/barrier.saveBarrier',
                    JSON.stringify($that.addBarrierInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addBarrierModel').modal('hide');
                            $that.clearAddBarrierInfo();
                            vc.emit('barrier', 'listBarrier', {});

                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddBarrierInfo: function () {
                $that.addBarrierInfo = {
                    machineCode: '',
                    machineName: '',
                    machineIp: '',
                    direction: '',
                    implBean: '',
                    boxId: '',
                    hms: [],
                    parkingBoxs:[]
                };
            },
            _listAddHms: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        hmType: '2002'
                    }
                };

                //发送get请求
                vc.http.apiGet('/hm.listHardwareManufacturer',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.addBarrierInfo.hms = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAddParkingBoxs: function(_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:50,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/parkingBox.listParkingBox',
                    param,
                    function(json, res) {
                        var _parkingBoxInfo = JSON.parse(json);
                        $that.addBarrierInfo.parkingBoxs = _parkingBoxInfo.data;
                      
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);
