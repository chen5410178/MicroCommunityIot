(function (vc) {

    vc.extends({
        data: {
            addParkingRegionInfo: {
                prId: '',
                regionCode: '',
                paId: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addParkingRegion', 'openAddParkingRegionModal', function (_area) {
                $that.addParkingRegionInfo.paId = _area.paId;
                $('#addParkingRegionModel').modal('show');
            });
        },
        methods: {
            addParkingRegionValidate() {
                return vc.validate.validate({
                    addParkingRegionInfo: $that.addParkingRegionInfo
                }, {
                    'addParkingRegionInfo.regionCode': [
                        {
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
                    'addParkingRegionInfo.paId': [
                        {
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
                    'addParkingRegionInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "300",
                            errInfo: "备注不能超过300"
                        },
                    ],




                });
            },
            saveParkingRegionInfo: function () {
                if (!$that.addParkingRegionValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addParkingRegionInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/parkingRegion.saveParkingRegion',
                    JSON.stringify($that.addParkingRegionInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addParkingRegionModel').modal('hide');
                            $that.clearAddParkingRegionInfo();
                            vc.emit('parkingRegion', 'listParkingRegion', {});
                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddParkingRegionInfo: function () {
                $that.addParkingRegionInfo = {
                    regionCode: '',
                    paId: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);
