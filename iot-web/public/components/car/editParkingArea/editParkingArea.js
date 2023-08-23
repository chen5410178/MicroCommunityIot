(function(vc, vm) {

    vc.extends({
        data: {
            editParkingAreaInfo: {
                paId: '',
                num: '',
                typeCd: '',
                extPaId: '',
                remark: '',

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('editParkingArea', 'openEditParkingAreaModal', function(_params) {
                $that.refreshEditParkingAreaInfo();
                $('#editParkingAreaModel').modal('show');
                vc.copyObject(_params, $that.editParkingAreaInfo);
                $that.editParkingAreaInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editParkingAreaValidate: function() {
                return vc.validate.validate({
                    editParkingAreaInfo: $that.editParkingAreaInfo
                }, {
                    'editParkingAreaInfo.num': [{
                            limit: "required",
                            param: "",
                            errInfo: "停车场编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "停车场编号不能超过12"
                        },
                    ],
                    'editParkingAreaInfo.typeCd': [{
                            limit: "required",
                            param: "",
                            errInfo: "类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "停车场类型不能超过12"
                        },
                    ],
                    'editParkingAreaInfo.extPaId': [{
                        limit: "maxLength",
                        param: "64",
                        errInfo: "三方停车场ID不能超过64"
                    }, ],
                    'editParkingAreaInfo.remark': [{
                        limit: "maxLength",
                        param: "300",
                        errInfo: "备注不能超过300"
                    }, ],
                    'editParkingAreaInfo.paId': [{
                        limit: "required",
                        param: "",
                        errInfo: "编号不能为空"
                    }]

                });
            },
            editParkingArea: function() {
                if (!$that.editParkingAreaValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/parkingArea.updateParkingArea',
                    JSON.stringify($that.editParkingAreaInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editParkingAreaModel').modal('hide');
                            vc.emit('parkingArea', 'listParkingArea', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditParkingAreaInfo: function() {
                $that.editParkingAreaInfo = {
                    paId: '',
                    num: '',
                    typeCd: '',
                    extPaId: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);