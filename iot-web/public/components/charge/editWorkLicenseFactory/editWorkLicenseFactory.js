(function (vc, vm) {

    vc.extends({
        data: {
            editWorkLicenseFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editWorkLicenseFactory', 'openEditWorkLicenseFactoryModal', function (_params) {
                $that.refreshEditWorkLicenseFactoryInfo();
                $('#editWorkLicenseFactoryModel').modal('show');
                vc.copyObject(_params, $that.editWorkLicenseFactoryInfo);
            });
        },
        methods: {
            editWorkLicenseFactoryValidate: function () {
                return vc.validate.validate({
                    editWorkLicenseFactoryInfo: $that.editWorkLicenseFactoryInfo
                }, {
                    'editWorkLicenseFactoryInfo.factoryName': [
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
                    'editWorkLicenseFactoryInfo.beanImpl': [
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
                    'editWorkLicenseFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editWorkLicenseFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            editWorkLicenseFactory: function () {
                if (!$that.editWorkLicenseFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/workLicenseFactory.updateWorkLicenseFactory',
                    JSON.stringify($that.editWorkLicenseFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editWorkLicenseFactoryModel').modal('hide');
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
            refreshEditWorkLicenseFactoryInfo: function () {
                $that.editWorkLicenseFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
