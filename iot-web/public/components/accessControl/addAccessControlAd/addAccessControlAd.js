(function (vc) {

    vc.extends({
        data: {
            addAccessControlAdInfo: {
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
            vc.on('addAccessControlAd', 'openAddAccessControlAdModal', function () {
                $('#addAccessControlAdModel').modal('show');
            });
            vc.on('addAccessControlAd', 'notifyUrl', function (_param) {
                $that.addAccessControlAdInfo.url = _param.realFileName;
            })
        },
        methods: {
            addAccessControlAdValidate() {
                return vc.validate.validate({
                    addAccessControlAdInfo: $that.addAccessControlAdInfo
                }, {
                    'addAccessControlAdInfo.adName': [
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
                    'addAccessControlAdInfo.state': [
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
                    'addAccessControlAdInfo.adType': [
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
                    'addAccessControlAdInfo.url': [
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
                    'addAccessControlAdInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveAccessControlAdInfo: function () {
                if (!$that.addAccessControlAdValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addAccessControlAdInfo.communityId = vc.getCurrentCommunity().communityId;


                vc.http.apiPost(
                    '/ad.saveAccessControlAd',
                    JSON.stringify($that.addAccessControlAdInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addAccessControlAdModel').modal('hide');
                            $that.clearAddAccessControlAdInfo();
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
            clearAddAccessControlAdInfo: function () {
                $that.addAccessControlAdInfo = {
                    adName: '',
                    state: '',
                    adType: '',
                    url: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
