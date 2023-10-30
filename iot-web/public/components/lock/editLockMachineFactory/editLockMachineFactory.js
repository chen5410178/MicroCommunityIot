(function (vc, vm) {

    vc.extends({
        data: {
            editLockMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editLockMachineFactory', 'openEditLockMachineFactoryModal', function (_params) {
                $that.refreshEditLockMachineFactoryInfo();
                $('#editLockMachineFactoryModel').modal('show');
                vc.copyObject(_params, $that.editLockMachineFactoryInfo);
            });
        },
        methods: {
            editLockMachineFactoryValidate: function () {
                return vc.validate.validate({
                    editLockMachineFactoryInfo: $that.editLockMachineFactoryInfo
                }, {
                    'editLockMachineFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家ID不能为空"
                        },
                    ],
                    'editLockMachineFactoryInfo.factoryName': [
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
                    'editLockMachineFactoryInfo.beanImpl': [
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
                    'editLockMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editLockMachineFactory: function () {
                if (!$that.editLockMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/lockMachineFactory.updateLockMachineFactory',
                    JSON.stringify($that.editLockMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editLockMachineFactoryModel').modal('hide');
                            vc.emit('lockMachineFactoryManage', 'listLockMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditLockMachineFactoryInfo: function () {
                $that.editLockMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
