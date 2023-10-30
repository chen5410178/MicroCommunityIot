(function (vc, vm) {

    vc.extends({
        data: {
            deleteLockMachineFactorySpecInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLockMachineFactorySpec', 'openDeleteLockMachineFactorySpecModal', function (_params) {
                $that.deleteLockMachineFactorySpecInfo = _params;
                $('#deleteLockMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            deleteLockMachineFactorySpec: function () {
                vc.http.apiPost(
                    '/lockMachineFactorySpec.deleteLockMachineFactorySpec',
                    JSON.stringify($that.deleteLockMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLockMachineFactorySpecModel').modal('hide');
                            vc.emit('lockMachineFactorySpecManage', 'listLockMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteLockMachineFactorySpecModel: function () {
                $('#deleteLockMachineFactorySpecModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
