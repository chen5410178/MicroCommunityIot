(function (vc) {

    vc.extends({
        data: {
            addPersonFaceQrcodeInfo: {
                pfqId: '',
                qrcodeName: '',
                smsValidate: '',
                telValidate: '',
                audit: '',
                invite: '',
                inviteCode: '',
                state: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addPersonFaceQrcode', 'openAddPersonFaceQrcodeModal', function () {
                $('#addPersonFaceQrcodeModel').modal('show');
            });
        },
        methods: {
            addPersonFaceQrcodeValidate() {
                return vc.validate.validate({
                    addPersonFaceQrcodeInfo: $that.addPersonFaceQrcodeInfo
                }, {
                    'addPersonFaceQrcodeInfo.qrcodeName': [
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
                    'addPersonFaceQrcodeInfo.smsValidate': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "短信验证不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "短信验证不能超过12"
                        },
                    ],
                    'addPersonFaceQrcodeInfo.telValidate': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "手机号验证不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "手机号验证不能超过12"
                        },
                    ],
                    'addPersonFaceQrcodeInfo.audit': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "审核不能超过12"
                        },
                    ],
                    'addPersonFaceQrcodeInfo.invite': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "邀请开关不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "邀请开关不能超过12"
                        },
                    ],
                    'addPersonFaceQrcodeInfo.inviteCode': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "邀请码不能超过64"
                        },
                    ],
                    'addPersonFaceQrcodeInfo.state': [
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
                    'addPersonFaceQrcodeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "说明不能超过512"
                        },
                    ],
                });
            },
            savePersonFaceQrcodeInfo: function () {
                if (!$that.addPersonFaceQrcodeValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addPersonFaceQrcodeInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/personFace.savePersonFaceQrcode',
                    JSON.stringify($that.addPersonFaceQrcodeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addPersonFaceQrcodeModel').modal('hide');
                            $that.clearAddPersonFaceQrcodeInfo();
                            vc.emit('personFaceQrcode', 'listPersonFaceQrcode', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddPersonFaceQrcodeInfo: function () {
                $that.addPersonFaceQrcodeInfo = {
                    qrcodeName: '',
                    smsValidate: '',
                    telValidate: '',
                    audit: '',
                    invite: '',
                    inviteCode: '',
                    state: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
