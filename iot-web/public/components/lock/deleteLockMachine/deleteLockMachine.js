(function (vc, vm) {

    vc.extends({
        data: {
            deleteLockMachineInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLockMachine', 'openDeleteLockMachineModal', function (_params) {
                $that.deleteLockMachineInfo = _params;
                $('#deleteLockMachineModel').modal('show');
            });
        },
        methods: {
            deleteLockMachine: function () {
                vc.http.apiPost(
                    '/lockMachine.deleteLockMachine',
                    JSON.stringify($that.deleteLockMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLockMachineModel').modal('hide');
                            vc.emit('lockMachineManage', 'listLockMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteLockMachineModel: function () {
                $('#deleteLockMachineModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
