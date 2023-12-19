(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMeterQrcodeInfo: {
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
            vc.on('addMeterQrcode', 'openAddMeterQrcodeModal', function () {
                $('#addMeterQrcodeModel').modal('show');
            });
        },
        methods: {
            addMeterQrcodeValidate() {
                return vc.validate.validate({
                    addMeterQrcodeInfo: $that.addMeterQrcodeInfo
                }, {
                    'addMeterQrcodeInfo.qrcodeName': [
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
                    'addMeterQrcodeInfo.communityId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小区ID不能为空"
                        },
                    ],
                    'addMeterQrcodeInfo.queryWay': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "查询方式不能为空"
                        },
                    ],
                    'addMeterQrcodeInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态不能为空"
                        },
                    ],
                    'addMeterQrcodeInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "说明不能超过512"
                        },
                    ],
                });
            },
            saveMeterQrcodeInfo: function () {
                $that.addMeterQrcodeInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addMeterQrcodeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addMeterQrcodeInfo);
                    $('#addMeterQrcodeModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/meterQrcode.saveMeterQrcode',
                    JSON.stringify($that.addMeterQrcodeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addMeterQrcodeModel').modal('hide');
                            $that.clearAddMeterQrcodeInfo();
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
            clearAddMeterQrcodeInfo: function () {
                $that.addMeterQrcodeInfo = {
                    mqId: '',
                    qrcodeName: '',
                    communityId: '',
                    queryWay: '',
                    createStaffId: '',
                    createStaffName: '',
                    state: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
