(function (vc, vm) {

    vc.extends({
        data: {
            editMeterMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                machineModel: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editMeterMachineFactory', 'openEditMeterMachineFactoryModal', function (_params) {
                $that.refreshEditMeterMachineFactoryInfo();
                $('#editMeterMachineFactoryModel').modal('show');
                vc.copyObject(_params, $that.editMeterMachineFactoryInfo);
            });
        },
        methods: {
            editMeterMachineFactoryValidate: function () {
                return vc.validate.validate({
                    editMeterMachineFactoryInfo: $that.editMeterMachineFactoryInfo
                }, {
                    'editMeterMachineFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家ID不能为空"
                        },
                    ],
                    'editMeterMachineFactoryInfo.factoryName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "厂家名称不能超过64"
                        },
                    ],
                    'editMeterMachineFactoryInfo.beanImpl': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家处理类不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "厂家处理类不能超过512"
                        },
                    ],
                    'editMeterMachineFactoryInfo.machineModel': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模式不能为空"
                        },
                    ],
                    'editMeterMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editMeterMachineFactory: function () {
                if (!$that.editMeterMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/meterMachineFactory.updateMeterMachineFactory',
                    JSON.stringify($that.editMeterMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editMeterMachineFactoryModel').modal('hide');
                            vc.emit('meterMachineFactoryManage', 'listMeterMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditMeterMachineFactoryInfo: function () {
                $that.editMeterMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    machineModel: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
