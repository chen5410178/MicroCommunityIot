(function (vc, vm) {

    vc.extends({
        data: {
            deleteChargeMachineFactorySpecInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteChargeMachineFactorySpec', 'openDeleteChargeMachineFactorySpecModal', function (_params) {
                $that.deleteChargeMachineFactorySpecInfo = _params;
                $('#deleteChargeMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            deleteChargeMachineFactorySpec: function () {
                vc.http.apiPost(
                    '/chargeMachineFactorySpec.deleteChargeMachineFactorySpec',
                    JSON.stringify($that.deleteChargeMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteChargeMachineFactorySpecModel').modal('hide');
                            vc.emit('chargeMachineFactorySpecManage', 'listChargeMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteChargeMachineFactorySpecModel: function () {
                $('#deleteChargeMachineFactorySpecModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
