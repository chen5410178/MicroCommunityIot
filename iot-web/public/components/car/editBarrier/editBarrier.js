(function (vc, vm) {

    vc.extends({
        data: {
            editBarrierInfo: {
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
            vc.on('editBarrier', 'openEditBarrierModal', function (_params) {
                $that.refreshEditBarrierInfo();
                $that._listEditHms();
                $that._listEditParkingBoxs();
                $('#editBarrierModel').modal('show');
                vc.copyObject(_params, $that.editBarrierInfo);
                $that.editBarrierInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editBarrierValidate: function () {
                return vc.validate.validate({
                    editBarrierInfo: $that.editBarrierInfo
                }, {
                    'editBarrierInfo.machineCode': [
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
                    'editBarrierInfo.machineName': [
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
                    'editBarrierInfo.machineIp': [
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
                    'editBarrierInfo.direction': [
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
                    'editBarrierInfo.implBean': [
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
                    'editBarrierInfo.boxId': [
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
                    'editBarrierInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editBarrier: function () {
                if (!$that.editBarrierValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/barrier.updateBarrier',
                    JSON.stringify($that.editBarrierInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editBarrierModel').modal('hide');
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
            refreshEditBarrierInfo: function () {
                $that.editBarrierInfo = {
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
            _listEditHms: function(_page, _rows) {
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
                        $that.editBarrierInfo.hms = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listEditParkingBoxs: function(_page, _rows) {
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
                        $that.editBarrierInfo.parkingBoxs = _parkingBoxInfo.data;
                      
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc, window.$that);
