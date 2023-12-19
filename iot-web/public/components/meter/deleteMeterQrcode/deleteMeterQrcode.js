(function (vc, vm) {

    vc.extends({
        data: {
            deleteMeterQrcodeInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMeterQrcode', 'openDeleteMeterQrcodeModal', function (_params) {
                $that.deleteMeterQrcodeInfo = _params;
                $('#deleteMeterQrcodeModel').modal('show');
            });
        },
        methods: {
            deleteMeterQrcode: function () {
                vc.http.apiPost(
                    '/meterQrcode.deleteMeterQrcode',
                    JSON.stringify($that.deleteMeterQrcodeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMeterQrcodeModel').modal('hide');
                            vc.emit('meterQrcodeManage', 'listMeterQrcode', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteMeterQrcodeModel: function () {
                $('#deleteMeterQrcodeModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
