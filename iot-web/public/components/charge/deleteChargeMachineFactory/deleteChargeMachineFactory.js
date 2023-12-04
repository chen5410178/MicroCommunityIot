(function (vc, vm) {

    vc.extends({
        data: {
            deleteChargeMachineFactoryInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteChargeMachineFactory', 'openDeleteChargeMachineFactoryModal', function (_params) {
                $that.deleteChargeMachineFactoryInfo = _params;
                $('#deleteChargeMachineFactoryModel').modal('show');
            });
        },
        methods: {
            deleteChargeMachineFactory: function () {
                vc.http.apiPost(
                    '/chargeMachine.deleteChargeMachineFactory',
                    JSON.stringify($that.deleteChargeMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteChargeMachineFactoryModel').modal('hide');
                            vc.emit('chargeMachineFactoryManage', 'listChargeMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteChargeMachineFactoryModel: function () {
                $('#deleteChargeMachineFactoryModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
