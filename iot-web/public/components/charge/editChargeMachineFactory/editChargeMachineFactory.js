(function (vc, vm) {

    vc.extends({
        data: {
            editChargeMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editChargeMachineFactory', 'openEditChargeMachineFactoryModal', function (_params) {
                $that.refreshEditChargeMachineFactoryInfo();
                $('#editChargeMachineFactoryModel').modal('show');
                vc.copyObject(_params, $that.editChargeMachineFactoryInfo);
            });
        },
        methods: {
            editChargeMachineFactoryValidate: function () {
                return vc.validate.validate({
                    editChargeMachineFactoryInfo: $that.editChargeMachineFactoryInfo
                }, {
                    'editChargeMachineFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家ID不能为空"
                        },
                    ],
                    'editChargeMachineFactoryInfo.factoryName': [
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
                    'editChargeMachineFactoryInfo.beanImpl': [
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
                    'editChargeMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editChargeMachineFactory: function () {
                if (!$that.editChargeMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/chargeMachine.updateChargeMachineFactory',
                    JSON.stringify($that.editChargeMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editChargeMachineFactoryModel').modal('hide');
                            vc.emit('chargeMachineFactoryManage', 'listChargeMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditChargeMachineFactoryInfo: function () {
                $that.editChargeMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
