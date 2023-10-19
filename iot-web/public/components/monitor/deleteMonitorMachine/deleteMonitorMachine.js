(function (vc, vm) {

    vc.extends({
        data: {
            deleteMonitorMachineInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMonitorMachine', 'openDeleteMonitorMachineModal', function (_params) {
                $that.deleteMonitorMachineInfo = _params;
                $('#deleteMonitorMachineModel').modal('show');
            });
        },
        methods: {
            deleteMonitorMachine: function () {
                vc.http.apiPost(
                    '/monitorMachine.deleteMonitorMachine',
                    JSON.stringify($that.deleteMonitorMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMonitorMachineModel').modal('hide');
                            vc.emit('monitorMachineManage', 'listMonitorMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            closeDeleteMonitorMachineModel: function () {
                $('#deleteMonitorMachineModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
