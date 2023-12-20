(function (vc, vm) {

    vc.extends({
        data: {
            auditPassQrcodeInfo: {
                pqId: '',
                personName: '',
                personTel: '',
                state:'',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('auditPassQrcode', 'openAuditPassQrcodeModal', function (_params) {
                $that.refreshAuditPassQrcodeInfo();
                $('#auditPassQrcodeModel').modal('show');
                vc.copyObject(_params, $that.auditPassQrcodeInfo);
                $that.auditPassQrcodeInfo.state = '';
                $that.auditPassQrcodeInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            auditPassQrcodeValidate: function () {
                return vc.validate.validate({
                    auditPassQrcodeInfo: $that.auditPassQrcodeInfo
                }, {
                    'auditPassQrcodeInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'auditPassQrcodeInfo.remark': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "审核说明不能为空"
                        }
                    ],
                    'auditPassQrcodeInfo.pqId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            auditPassQrcode: function () {
                if (!$that.auditPassQrcodeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/passcode.auditPassQrcode',
                    JSON.stringify($that.auditPassQrcodeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#auditPassQrcodeModel').modal('hide');
                            vc.emit('passQrcode', 'listPassQrcode', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshAuditPassQrcodeInfo: function () {
                $that.auditPassQrcodeInfo = {
                    pqId: '',
                    personName: '',
                    personTel: '',
                    state:'',
                    remark: '',
                }
            }
        }
    });

})(window.vc, window.$that);
