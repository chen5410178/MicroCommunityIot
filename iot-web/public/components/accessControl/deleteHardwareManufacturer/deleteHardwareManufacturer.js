(function (vc, vm) {

    vc.extends({
        data: {
            deleteHardwareManufacturerInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteHardwareManufacturer', 'openDeleteHardwareManufacturerModal', function (_params) {
                $that.deleteHardwareManufacturerInfo = _params;
                $('#deleteHardwareManufacturerModel').modal('show');
            });
        },
        methods: {
            deleteHardwareManufacturer: function () {
                vc.http.apiPost(
                    '/hm.deleteHardwareManufacturer',
                    JSON.stringify($that.deleteHardwareManufacturerInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteHardwareManufacturerModel').modal('hide');
                            vc.emit('hardwareManufacturerManage', 'listHardwareManufacturer', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteHardwareManufacturerModel: function () {
                $('#deleteHardwareManufacturerModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
