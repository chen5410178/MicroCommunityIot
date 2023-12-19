(function (vc, vm) {

    vc.extends({
        data: {
            editPersonFaceQrcodeInfo: {
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
            vc.on('editPersonFaceQrcode', 'openEditPersonFaceQrcodeModal', function (_params) {
                $that.refreshEditPersonFaceQrcodeInfo();
                $('#editPersonFaceQrcodeModel').modal('show');
                vc.copyObject(_params, $that.editPersonFaceQrcodeInfo);
                $that.editPersonFaceQrcodeInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editPersonFaceQrcodeValidate: function () {
                return vc.validate.validate({
                    editPersonFaceQrcodeInfo: $that.editPersonFaceQrcodeInfo
                }, {
                    'editPersonFaceQrcodeInfo.qrcodeName': [
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
                    'editPersonFaceQrcodeInfo.smsValidate': [
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
                    'editPersonFaceQrcodeInfo.telValidate': [
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
                    'editPersonFaceQrcodeInfo.audit': [
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
                    'editPersonFaceQrcodeInfo.invite': [
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
                    'editPersonFaceQrcodeInfo.inviteCode': [
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "邀请码不能超过64"
                        },
                    ],
                    'editPersonFaceQrcodeInfo.state': [
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
                    'editPersonFaceQrcodeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "说明不能超过512"
                        },
                    ],
                    'editPersonFaceQrcodeInfo.pfqId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editPersonFaceQrcode: function () {
                if (!$that.editPersonFaceQrcodeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/personFace.updatePersonFaceQrcode',
                    JSON.stringify($that.editPersonFaceQrcodeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editPersonFaceQrcodeModel').modal('hide');
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
            refreshEditPersonFaceQrcodeInfo: function () {
                $that.editPersonFaceQrcodeInfo = {
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
            }
        }
    });

})(window.vc, window.$that);
