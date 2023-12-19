(function (vc, vm) {

    vc.extends({
        data: {
            editMeterQrcodeInfo: {
                mqId: '',
                qrcodeName: '',
                communityId: '',
                queryWay: '',
                createStaffId: '',
                createStaffName: '',
                state: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editMeterQrcode', 'openEditMeterQrcodeModal', function (_params) {
                $that.refreshEditMeterQrcodeInfo();
                $('#editMeterQrcodeModel').modal('show');
                vc.copyObject(_params, $that.editMeterQrcodeInfo);
            });
        },
        methods: {
            editMeterQrcodeValidate: function () {
                return vc.validate.validate({
                    editMeterQrcodeInfo: $that.editMeterQrcodeInfo
                }, {
                    'editMeterQrcodeInfo.mqId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        },
                    ],
                    'editMeterQrcodeInfo.qrcodeName': [
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
                    'editMeterQrcodeInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'editMeterQrcodeInfo.queryWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "查询方式不能为空"
                        },
                    ],
                    'editMeterQrcodeInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'editMeterQrcodeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "说明不能超过512"
                        },
                    ],
                });
            },
            editMeterQrcode: function () {
                if (!$that.editMeterQrcodeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/meterQrcode.updateMeterQrcode',
                    JSON.stringify($that.editMeterQrcodeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editMeterQrcodeModel').modal('hide');
                            vc.emit('meterQrcodeManage', 'listMeterQrcode', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditMeterQrcodeInfo: function () {
                $that.editMeterQrcodeInfo = {
                    mqId: '',
                    qrcodeName: '',
                    communityId: '',
                    queryWay: '',
                    createStaffId: '',
                    createStaffName: '',
                    state: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
