(function (vc, vm) {

    vc.extends({
        data: {
            deleteMonitorAreaInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMonitorArea', 'openDeleteMonitorAreaModal', function (_params) {
                $that.deleteMonitorAreaInfo = _params;
                $('#deleteMonitorAreaModel').modal('show');
            });
        },
        methods: {
            deleteMonitorArea: function () {
                vc.http.apiPost(
                    '/monitorArea.deleteMonitorArea',
                    JSON.stringify($that.deleteMonitorAreaInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMonitorAreaModel').modal('hide');
                            vc.emit('monitorAreaManage', 'listMonitorArea', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            closeDeleteMonitorAreaModel: function () {
                $('#deleteMonitorAreaModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
