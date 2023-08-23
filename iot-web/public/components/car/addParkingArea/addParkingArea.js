(function(vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addParkingAreaInfo: {
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
            vc.on('addParkingArea', 'openAddParkingAreaModal', function() {
                $('#addParkingAreaModel').modal('show');
            });
        },
        methods: {
            addParkingAreaValidate() {
                return vc.validate.validate({
                    addParkingAreaInfo: $that.addParkingAreaInfo
                }, {
                    'addParkingAreaInfo.num': [{
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
                    'addParkingAreaInfo.typeCd': [{
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
                    'addParkingAreaInfo.extPaId': [{
                        limit: "maxLength",
                        param: "64",
                        errInfo: "三方停车场ID不能超过64"
                    }, ],
                    'addParkingAreaInfo.remark': [{
                        limit: "maxLength",
                        param: "300",
                        errInfo: "备注不能超过300"
                    }, ],
                });
            },
            saveParkingAreaInfo: function() {
                if (!$that.addParkingAreaValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addParkingAreaInfo.communityId = vc.getCurrentCommunity().communityId;


                vc.http.apiPost(
                    '/parkingArea.saveParkingArea',
                    JSON.stringify($that.addParkingAreaInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addParkingAreaModel').modal('hide');
                            $that.clearAddParkingAreaInfo();
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
            clearAddParkingAreaInfo: function() {
                $that.addParkingAreaInfo = {
                    num: '',
                    typeCd: '',
                    extPaId: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);