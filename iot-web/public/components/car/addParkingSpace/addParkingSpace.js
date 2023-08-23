(function(vc) {

    vc.extends({
        data: {
            addParkingSpaceInfo: {
                psId: '',
                num: '',
                paId: '',
                state: 'F',
                remark: '',
                parkingType: '',
                parkingAreas: []
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('addParkingSpace', 'openAddParkingSpaceModal', function() {
                $that._loadAddParkingAreas();
                $('#addParkingSpaceModel').modal('show');
            });
        },
        methods: {
            addParkingSpaceValidate() {
                return vc.validate.validate({
                    addParkingSpaceInfo: $that.addParkingSpaceInfo
                }, {
                    'addParkingSpaceInfo.num': [{
                            limit: "required",
                            param: "",
                            errInfo: "车位编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "车位编号不能超过12"
                        },
                    ],
                    'addParkingSpaceInfo.paId': [{
                            limit: "required",
                            param: "",
                            errInfo: "停车场不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "停车场不能超过30"
                        },
                    ],
                    'addParkingSpaceInfo.state': [{
                            limit: "required",
                            param: "",
                            errInfo: "车位状态不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "4",
                            errInfo: "车位状态不能超过4"
                        },
                    ],
                    'addParkingSpaceInfo.remark': [{
                        limit: "maxLength",
                        param: "300",
                        errInfo: "备注不能超过300"
                    }, ],
                    'addParkingSpaceInfo.parkingType': [{
                            limit: "required",
                            param: "",
                            errInfo: "车位类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "3",
                            errInfo: "车位类型不能超过3"
                        },
                    ],
                });
            },
            saveParkingSpaceInfo: function() {
                if (!$that.addParkingSpaceValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addParkingSpaceInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/parkingSpace.saveParkingSpace',
                    JSON.stringify($that.addParkingSpaceInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addParkingSpaceModel').modal('hide');
                            $that.clearAddParkingSpaceInfo();
                            vc.emit('parkingSpace', 'listParkingSpace', {});

                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddParkingSpaceInfo: function() {
                $that.addParkingSpaceInfo = {
                    num: '',
                    paId: '',
                    state: 'F',
                    remark: '',
                    parkingType: '',
                    parkingAreas: []
                };
            },
            _loadAddParkingAreas: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/parkingArea.listParkingArea',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.addParkingSpaceInfo.parkingAreas = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);