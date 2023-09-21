(function (vc, vm) {

    vc.extends({
        data: {
            editLampMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLampMachineFactory', 'openEditLampMachineFactoryModal', function (_params) {
                $that.refreshEditLampMachineFactoryInfo();
                $('#editLampMachineFactoryModel').modal('show');
                vc.copyObject(_params, $that.editLampMachineFactoryInfo);
                $that.editLampMachineFactoryInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editLampMachineFactoryValidate: function () {
                return vc.validate.validate({
                    editLampMachineFactoryInfo: $that.editLampMachineFactoryInfo
                }, {
                    'editLampMachineFactoryInfo.factoryName': [
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
                    'editLampMachineFactoryInfo.beanImpl': [
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
                    'editLampMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editLampMachineFactory: function () {
                if (!$that.editLampMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lampMachineFactory.updateLampMachineFactory',
                    JSON.stringify($that.editLampMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editLampMachineFactoryModel').modal('hide');
                            vc.emit('lampMachineFactoryManage', 'listLampMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditLampMachineFactoryInfo: function () {
                $that.editLampMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                }
            }
        }
    });

})(window.vc, window.$that);
