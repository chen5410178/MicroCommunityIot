(function (vc, vm) {

    vc.extends({
        data: {
            deleteInstrumentFactoryInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteInstrumentFactory', 'openDeleteInstrumentFactoryModal', function (_params) {
                $that.deleteInstrumentFactoryInfo = _params;
                $('#deleteInstrumentFactoryModel').modal('show');
            });
        },
        methods: {
            deleteInstrumentFactory: function () {
                vc.http.apiPost(
                    '/instrumentFactory.deleteInstrumentFactory',
                    JSON.stringify($that.deleteInstrumentFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteInstrumentFactoryModel').modal('hide');
                            vc.emit('instrumentFactoryManage', 'listInstrumentFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteInstrumentFactoryModel: function () {
                $('#deleteInstrumentFactoryModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
