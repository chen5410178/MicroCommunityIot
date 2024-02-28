(function (vc) {

    vc.extends({
        data: {
            addWorkLicenseFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addWorkLicenseFactory', 'openAddWorkLicenseFactoryModal', function () {
                $('#addWorkLicenseFactoryModel').modal('show');
            });
        },
        methods: {
            addWorkLicenseFactoryValidate() {
                return vc.validate.validate({
                    addWorkLicenseFactoryInfo: $that.addWorkLicenseFactoryInfo
                }, {
                    'addWorkLicenseFactoryInfo.factoryName': [
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
                    'addWorkLicenseFactoryInfo.beanImpl': [
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
                    'addWorkLicenseFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveWorkLicenseFactoryInfo: function () {
                if (!$that.addWorkLicenseFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.http.apiPost(
                    '/workLicenseFactory.saveWorkLicenseFactory',
                    JSON.stringify($that.addWorkLicenseFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addWorkLicenseFactoryModel').modal('hide');
                            $that.clearAddWorkLicenseFactoryInfo();
                            vc.emit('workLicenseFactory', 'listWorkLicenseFactory', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddWorkLicenseFactoryInfo: function () {
                $that.addWorkLicenseFactoryInfo = {
                    factoryName: '',
                    beanImpl: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
