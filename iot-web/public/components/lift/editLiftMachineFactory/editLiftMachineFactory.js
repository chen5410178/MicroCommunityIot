(function (vc, vm) {

    vc.extends({
        data: {
            editLiftMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
                statusCd: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLiftMachineFactory', 'openEditLiftMachineFactoryModal', function (_params) {
                vc.component.refreshEditLiftMachineFactoryInfo();
                $('#editLiftMachineFactoryModel').modal('show');
                vc.copyObject(_params, vc.component.editLiftMachineFactoryInfo);
                vc.component.editLiftMachineFactoryInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editLiftMachineFactoryValidate: function () {
                return vc.validate.validate({
                    editLiftMachineFactoryInfo: vc.component.editLiftMachineFactoryInfo
                }, {
                    'editLiftMachineFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家ID不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "厂家ID不能超过30"
                        },
                    ],
                    'editLiftMachineFactoryInfo.factoryName': [
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
                    'editLiftMachineFactoryInfo.beanImpl': [
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
                    'editLiftMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editLiftMachineFactory: function () {
                if (!vc.component.editLiftMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'liftMachineFactory.updateLiftMachineFactory',
                    JSON.stringify(vc.component.editLiftMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editLiftMachineFactoryModel').modal('hide');
                            vc.emit('liftMachineFactoryManage', 'listLiftMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditLiftMachineFactoryInfo: function () {
                vc.component.editLiftMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                    statusCd: '',
                }
            }
        }
    });

})(window.vc, window.vc.component);
