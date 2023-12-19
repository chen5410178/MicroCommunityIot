(function (vc, vm) {

    vc.extends({
        data: {
            editPassQrcodeSettingInfo: {
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
            vc.on('editPassQrcodeSetting', 'openEditPassQrcodeSettingModal', function (_params) {
                $that.refreshEditPassQrcodeSettingInfo();
                $('#editPassQrcodeSettingModel').modal('show');
                vc.copyObject(_params, $that.editPassQrcodeSettingInfo);
                $that.editPassQrcodeSettingInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editPassQrcodeSettingValidate: function () {
                return vc.validate.validate({
                    editPassQrcodeSettingInfo: $that.editPassQrcodeSettingInfo
                }, {
                    'editPassQrcodeSettingInfo.qrcodeName': [
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
                    'editPassQrcodeSettingInfo.smsValidate': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "验证不能为空"
                        },
                    ],
                    'editPassQrcodeSettingInfo.audit': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核不能为空"
                        },
                    ],
                    'editPassQrcodeSettingInfo.expired': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "失效时间不能为空"
                        },
                    ],
                    'editPassQrcodeSettingInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'editPassQrcodeSettingInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "说明不能超过512"
                        },
                    ],
                    'editPassQrcodeSettingInfo.pqsId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editPassQrcodeSetting: function () {
                if (!$that.editPassQrcodeSettingValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/passcode.updatePassQrcode',
                    JSON.stringify($that.editPassQrcodeSettingInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editPassQrcodeSettingModel').modal('hide');
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
            refreshEditPassQrcodeSettingInfo: function () {
                $that.editPassQrcodeSettingInfo = {
                    pqsId: '',
                    qrcodeName: '',
                    smsValidate: '',
                    audit: '',
                    expired: '',
                    state: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
