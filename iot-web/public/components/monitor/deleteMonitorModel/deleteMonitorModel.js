(function (vc, vm) {

    vc.extends({
        data: {
            deleteMonitorModelInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMonitorModel', 'openDeleteMonitorModelModal', function (_params) {
                $that.deleteMonitorModelInfo = _params;
                $('#deleteMonitorModelModel').modal('show');
            });
        },
        methods: {
            deleteMonitorModel: function () {
                vc.http.apiPost(
                    '/monitorModel.deleteMonitorModel',
                    JSON.stringify($that.deleteMonitorModelInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMonitorModelModel').modal('hide');
                            vc.emit('monitorModelManage', 'listMonitorModel', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteMonitorModelModel: function () {
                $('#deleteMonitorModelModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
