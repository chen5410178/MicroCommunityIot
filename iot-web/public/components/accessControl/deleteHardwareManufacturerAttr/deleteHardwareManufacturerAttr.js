(function (vc, vm) {

    vc.extends({
        data: {
            deleteHardwareManufacturerAttrInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteHardwareManufacturerAttr', 'openDeleteHardwareManufacturerAttrModal', function (_params) {
                $that.deleteHardwareManufacturerAttrInfo = _params;
                $('#deleteHardwareManufacturerAttrModel').modal('show');
            });
        },
        methods: {
            deleteHardwareManufacturerAttr: function () {
                vc.http.apiPost(
                    '/hm.deleteHardwareManufacturerAttr',
                    JSON.stringify($that.deleteHardwareManufacturerAttrInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteHardwareManufacturerAttrModel').modal('hide');
                            vc.emit('hardwareManufacturerAttrManage', 'listHardwareManufacturerAttr', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteHardwareManufacturerAttrModel: function () {
                $('#deleteHardwareManufacturerAttrModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
