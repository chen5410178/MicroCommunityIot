(function (vc, vm) {

    vc.extends({
        data: {
            deleteLockMachineFactoryInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLockMachineFactory', 'openDeleteLockMachineFactoryModal', function (_params) {
                $that.deleteLockMachineFactoryInfo = _params;
                $('#deleteLockMachineFactoryModel').modal('show');
            });
        },
        methods: {
            deleteLockMachineFactory: function () {
                vc.http.apiPost(
                    '/lockMachineFactory.deleteLockMachineFactory',
                    JSON.stringify($that.deleteLockMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLockMachineFactoryModel').modal('hide');
                            vc.emit('lockMachineFactoryManage', 'listLockMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteLockMachineFactoryModel: function () {
                $('#deleteLockMachineFactoryModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
