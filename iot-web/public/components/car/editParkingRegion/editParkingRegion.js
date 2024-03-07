(function (vc, vm) {

    vc.extends({
        data: {
            editParkingRegionInfo: {
                prId: '',
                regionCode: '',
                paId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editParkingRegion', 'openEditParkingRegionModal', function (_params) {
                $that.refreshEditParkingRegionInfo();
                $('#editParkingRegionModel').modal('show');
                vc.copyObject(_params, $that.editParkingRegionInfo);
                $that.editParkingRegionInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editParkingRegionValidate: function () {
                return vc.validate.validate({
                    editParkingRegionInfo: $that.editParkingRegionInfo
                }, {
                    'editParkingRegionInfo.regionCode': [
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
                    'editParkingRegionInfo.paId': [
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
                    'editParkingRegionInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "300",
                            errInfo: "备注不能超过300"
                        },
                    ],
                    'editParkingRegionInfo.prId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editParkingRegion: function () {
                if (!$that.editParkingRegionValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'parkingRegion.updateParkingRegion',
                    JSON.stringify($that.editParkingRegionInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editParkingRegionModel').modal('hide');
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
            refreshEditParkingRegionInfo: function () {
                $that.editParkingRegionInfo = {
                    prId: '',
                    regionCode: '',
                    paId: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
