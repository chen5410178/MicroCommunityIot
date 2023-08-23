(function(vc, vm) {

    vc.extends({
        data: {
            editParkingSpaceInfo: {
                psId: '',
                num: '',
                paId: '',
                state: '',
                remark: '',
                parkingType: '',
                parkingAreas: []
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('editParkingSpace', 'openEditParkingSpaceModal', function(_params) {
                $that.refreshEditParkingSpaceInfo();
                $that._loadEditParkingAreas();
                $('#editParkingSpaceModel').modal('show');
                vc.copyObject(_params, $that.editParkingSpaceInfo);
                $that.editParkingSpaceInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editParkingSpaceValidate: function() {
                return vc.validate.validate({
                    editParkingSpaceInfo: $that.editParkingSpaceInfo
                }, {
                    'editParkingSpaceInfo.num': [{
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
                    'editParkingSpaceInfo.paId': [{
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
                    'editParkingSpaceInfo.state': [{
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
                    'editParkingSpaceInfo.remark': [{
                        limit: "maxLength",
                        param: "300",
                        errInfo: "备注不能超过300"
                    }, ],
                    'editParkingSpaceInfo.parkingType': [{
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
                    'editParkingSpaceInfo.psId': [{
                        limit: "required",
                        param: "",
                        errInfo: "编号不能为空"
                    }]

                });
            },
            editParkingSpace: function() {
                if (!$that.editParkingSpaceValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'parkingSpace.updateParkingSpace',
                    JSON.stringify($that.editParkingSpaceInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editParkingSpaceModel').modal('hide');
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
            refreshEditParkingSpaceInfo: function() {
                $that.editParkingSpaceInfo = {
                    psId: '',
                    num: '',
                    paId: '',
                    state: '',
                    remark: '',
                    parkingType: '',
                    parkingAreas: []
                }
            },
            _loadEditParkingAreas: function(_page, _rows) {

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
                        $that.editParkingSpaceInfo.parkingAreas = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc, window.$that);