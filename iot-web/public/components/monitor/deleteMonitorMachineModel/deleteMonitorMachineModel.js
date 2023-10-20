(function (vc, vm) {

    vc.extends({
        data: {
            deleteMonitorMachineModelInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMonitorMachineModel', 'openDeleteMonitorMachineModelModal', function (_params) {
                $that.deleteMonitorMachineModelInfo = _params;
                $('#deleteMonitorMachineModelModel').modal('show');
            });
        },
        methods: {
            deleteMonitorMachineModel: function () {
                $that.deleteMonitorMachineModelInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/monitorMachineModel.deleteMonitorMachineModel',
                    JSON.stringify($that.deleteMonitorMachineModelInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMonitorMachineModelModel').modal('hide');
                            vc.emit('monitorMachineModelManage', 'listMonitorMachineModel', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteMonitorMachineModelModel: function () {
                $('#deleteMonitorMachineModelModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
