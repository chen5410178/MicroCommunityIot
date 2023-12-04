(function (vc, vm) {

    vc.extends({
        data: {
            deleteMeterMachineFactoryInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMeterMachineFactory', 'openDeleteMeterMachineFactoryModal', function (_params) {
                $that.deleteMeterMachineFactoryInfo = _params;
                $('#deleteMeterMachineFactoryModel').modal('show');
            });
        },
        methods: {
            deleteMeterMachineFactory: function () {
                vc.http.apiPost(
                    '/meterMachineFactory.deleteMeterMachineFactory',
                    JSON.stringify($that.deleteMeterMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMeterMachineFactoryModel').modal('hide');
                            vc.emit('meterMachineFactoryManage', 'listMeterMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteMeterMachineFactoryModel: function () {
                $('#deleteMeterMachineFactoryModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
