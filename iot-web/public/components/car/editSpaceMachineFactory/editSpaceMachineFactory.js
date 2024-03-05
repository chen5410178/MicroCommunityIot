(function (vc, vm) {

    vc.extends({
        data: {
            editSpaceMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editSpaceMachineFactory', 'openEditSpaceMachineFactoryModal', function (_params) {
                $that.refreshEditSpaceMachineFactoryInfo();
                $('#editSpaceMachineFactoryModel').modal('show');
                vc.copyObject(_params, $that.editSpaceMachineFactoryInfo);
            });
        },
        methods: {
            editSpaceMachineFactoryValidate: function () {
                return vc.validate.validate({
                    editSpaceMachineFactoryInfo: $that.editSpaceMachineFactoryInfo
                }, {
                    'editSpaceMachineFactoryInfo.factoryName': [
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
                    'editSpaceMachineFactoryInfo.beanImpl': [
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
                    'editSpaceMachineFactoryInfo.remark': [
            
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editSpaceMachineFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editSpaceMachineFactory: function () {
                if (!$that.editSpaceMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/spaceMachineFactory.updateSpaceMachineFactory',
                    JSON.stringify($that.editSpaceMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editSpaceMachineFactoryModel').modal('hide');
                            vc.emit('spaceMachineFactory', 'listSpaceMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditSpaceMachineFactoryInfo: function () {
                $that.editSpaceMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
