(function (vc, vm) {

    vc.extends({
        data: {
            editInstrumentFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editInstrumentFactory', 'openEditInstrumentFactoryModal', function (_params) {
                $that.refreshEditInstrumentFactoryInfo();
                $('#editInstrumentFactoryModel').modal('show');
                vc.copyObject(_params, $that.editInstrumentFactoryInfo);
            });
        },
        methods: {
            editInstrumentFactoryValidate: function () {
                return vc.validate.validate({
                    editInstrumentFactoryInfo: $that.editInstrumentFactoryInfo
                }, {
                    'editInstrumentFactoryInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家ID不能为空"
                        },
                    ],
                    'editInstrumentFactoryInfo.factoryName': [
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
                    'editInstrumentFactoryInfo.beanImpl': [
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
                    'editInstrumentFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            editInstrumentFactory: function () {
                if (!$that.editInstrumentFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/instrumentFactory.updateInstrumentFactory',
                    JSON.stringify($that.editInstrumentFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#editInstrumentFactoryModel').modal('hide');
                            vc.emit('instrumentFactoryManage', 'listInstrumentFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditInstrumentFactoryInfo: function () {
                $that.editInstrumentFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                }
            }
        }
    });
})(window.vc, window.$that);
