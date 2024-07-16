(function (vc, vm) {

    vc.extends({
        data: {
            editAccessControlAdInfo: {
                adId: '',
                adName: '',
                state: '',
                adType: '',
                url: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editAccessControlAd', 'openEditAccessControlAdModal', function (_params) {
                $that.refreshEditAccessControlAdInfo();
                $('#editAccessControlAdModel').modal('show');
                vc.copyObject(_params, $that.editAccessControlAdInfo);
                $that.editAccessControlAdInfo.communityId = vc.getCurrentCommunity().communityId;
                if ($that.editAccessControlAdInfo.url) {
                    vc.emit('editAccessControlAd', 'uploadFile', 'notifyVedio', $that.editAccessControlAdInfo.url)
                }
            });
            vc.on('editAccessControlAd', 'notifyUrl', function (_param) {
                $that.editAccessControlAdInfo.url = _param.realFileName;
            })
        },
        methods: {
            editAccessControlAdValidate: function () {
                return vc.validate.validate({
                    editAccessControlAdInfo: $that.editAccessControlAdInfo
                }, {
                    'editAccessControlAdInfo.adName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "广告名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "广告名称不能超过64"
                        },
                    ],
                    'editAccessControlAdInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "状态不能超过12"
                        },
                    ],
                    'editAccessControlAdInfo.adType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "广告类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "广告类型不能超过12"
                        },
                    ],
                    'editAccessControlAdInfo.url': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "广告地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "广告地址不能超过512"
                        },
                    ],
                    'editAccessControlAdInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editAccessControlAdInfo.adId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editAccessControlAd: function () {
                if (!$that.editAccessControlAdValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/ad.updateAccessControlAd',
                    JSON.stringify($that.editAccessControlAdInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editAccessControlAdModel').modal('hide');
                            vc.emit('accessControlAd', 'listAccessControlAd', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditAccessControlAdInfo: function () {
                $that.editAccessControlAdInfo = {
                    adId: '',
                    adName: '',
                    state: '',
                    adType: '',
                    url: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
