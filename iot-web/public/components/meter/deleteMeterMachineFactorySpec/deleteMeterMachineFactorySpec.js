(function (vc, vm) {

    vc.extends({
        data: {
            deleteMeterMachineFactorySpecInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMeterMachineFactorySpec', 'openDeleteMeterMachineFactorySpecModal', function (_params) {
                $that.deleteMeterMachineFactorySpecInfo = _params;
                $('#deleteMeterMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            deleteMeterMachineFactorySpec: function () {
                vc.http.apiPost(
                    '/meterMachineFactorySpec.deleteMeterMachineFactorySpec',
                    JSON.stringify($that.deleteMeterMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteMeterMachineFactorySpecModel').modal('hide');
                            vc.emit('meterMachineFactorySpecManage', 'listMeterMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteMeterMachineFactorySpecModel: function () {
                $('#deleteMeterMachineFactorySpecModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
