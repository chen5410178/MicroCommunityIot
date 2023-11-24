(function (vc, vm) {

    vc.extends({
        data: {
            deleteInstrumentInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteInstrument', 'openDeleteInstrumentModal', function (_params) {
                $that.deleteInstrumentInfo = _params;
                $('#deleteInstrumentModel').modal('show');
            });
        },
        methods: {
            deleteInstrument: function () {
                vc.http.apiPost(
                    '/instrument.deleteInstrument',
                    JSON.stringify($that.deleteInstrumentInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteInstrumentModel').modal('hide');
                            vc.emit('instrumentManage', 'listInstrument', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteInstrumentModel: function () {
                $('#deleteInstrumentModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
