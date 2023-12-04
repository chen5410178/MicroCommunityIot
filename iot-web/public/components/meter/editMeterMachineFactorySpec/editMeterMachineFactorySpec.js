(function (vc, vm) {

    vc.extends({
        data: {
            editMeterMachineFactorySpecInfo: {
                specId: '',
                factoryId: '',
                specName: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editMeterMachineFactorySpec', 'openEditMeterMachineFactorySpecModal', function (_params) {
                $that.refreshEditMeterMachineFactorySpecInfo();
                $('#editMeterMachineFactorySpecModel').modal('show');
                vc.copyObject(_params, $that.editMeterMachineFactorySpecInfo);
            });
        },
        methods: {
            editMeterMachineFactorySpecValidate: function () {
                return vc.validate.validate({
                    editMeterMachineFactorySpecInfo: $that.editMeterMachineFactorySpecInfo
                }, {
                    'editMeterMachineFactorySpecInfo.specId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格ID不能为空"
                        },
                    ],
                    'editMeterMachineFactorySpecInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        },
                    ],
                    'editMeterMachineFactorySpecInfo.specName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "规格名称不能超过64"
                        },
                    ],
                    'editMeterMachineFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            editMeterMachineFactorySpec: function () {
                if (!$that.editMeterMachineFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/meterMachineFactorySpec.updateMeterMachineFactorySpec',
                    JSON.stringify($that.editMeterMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editMeterMachineFactorySpecModel').modal('hide');
                            vc.emit('meterMachineFactorySpecManage', 'listMeterMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditMeterMachineFactorySpecInfo: function () {
                $that.editMeterMachineFactorySpecInfo = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
