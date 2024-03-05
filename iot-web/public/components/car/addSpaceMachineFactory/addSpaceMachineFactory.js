(function (vc) {

    vc.extends({
        data: {
            addSpaceMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addSpaceMachineFactory', 'openAddSpaceMachineFactoryModal', function () {
                $('#addSpaceMachineFactoryModel').modal('show');
            });
        },
        methods: {
            addSpaceMachineFactoryValidate() {
                return vc.validate.validate({
                    addSpaceMachineFactoryInfo: $that.addSpaceMachineFactoryInfo
                }, {
                    'addSpaceMachineFactoryInfo.factoryName': [
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
                    'addSpaceMachineFactoryInfo.beanImpl': [
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
                    'addSpaceMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveSpaceMachineFactoryInfo: function () {
                if (!$that.addSpaceMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }


                vc.http.apiPost(
                    '/spaceMachineFactory.saveSpaceMachineFactory',
                    JSON.stringify($that.addSpaceMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addSpaceMachineFactoryModel').modal('hide');
                            $that.clearAddSpaceMachineFactoryInfo();
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
            clearAddSpaceMachineFactoryInfo: function () {
                $that.addSpaceMachineFactoryInfo = {
                    factoryName: '',
                    beanImpl: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
