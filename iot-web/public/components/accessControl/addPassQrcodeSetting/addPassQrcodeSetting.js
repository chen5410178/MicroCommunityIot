(function (vc) {

    vc.extends({
        data: {
            addPassQrcodeSettingInfo: {
                pqsId: '',
                qrcodeName: '',
                smsValidate: '',
                audit: '',
                expired: '',
                state: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addPassQrcodeSetting', 'openAddPassQrcodeSettingModal', function () {
                $('#addPassQrcodeSettingModel').modal('show');
            });
        },
        methods: {
            addPassQrcodeSettingValidate() {
                return vc.validate.validate({
                    addPassQrcodeSettingInfo: $that.addPassQrcodeSettingInfo
                }, {
                    'addPassQrcodeSettingInfo.qrcodeName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "128",
                            errInfo: "名称不能超过128"
                        },
                    ],
                    'addPassQrcodeSettingInfo.smsValidate': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "验证不能为空"
                        },
                    ],
                    'addPassQrcodeSettingInfo.audit': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核不能为空"
                        },
                    ],
                    'addPassQrcodeSettingInfo.expired': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "失效时间不能为空"
                        },
                    ],
                    'addPassQrcodeSettingInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'addPassQrcodeSettingInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "说明不能超过512"
                        },
                    ],
                });
            },
            savePassQrcodeSettingInfo: function () {
                if (!$that.addPassQrcodeSettingValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addPassQrcodeSettingInfo.communityId = vc.getCurrentCommunity().communityId;
          
                vc.http.apiPost(
                    '/passcode.savePassQrcodeSetting',
                    JSON.stringify($that.addPassQrcodeSettingInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addPassQrcodeSettingModel').modal('hide');
                            $that.clearAddPassQrcodeSettingInfo();
                            vc.emit('passQrcodeSetting', 'listPassQrcodeSetting', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddPassQrcodeSettingInfo: function () {
                $that.addPassQrcodeSettingInfo = {
                    qrcodeName: '',
                    smsValidate: '',
                    audit: '',
                    expired: '',
                    state: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
