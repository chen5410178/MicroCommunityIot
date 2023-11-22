(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addInstrumentFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addInstrumentFactory', 'openAddInstrumentFactoryModal', function () {
                $('#addInstrumentFactoryModel').modal('show');
            });
        },
        methods: {
            addInstrumentFactoryValidate() {
                return vc.validate.validate({
                    addInstrumentFactoryInfo: $that.addInstrumentFactoryInfo
                }, {
                    'addInstrumentFactoryInfo.factoryName': [
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
                    'addInstrumentFactoryInfo.beanImpl': [
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
                    'addInstrumentFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveInstrumentFactoryInfo: function () {
                if (!$that.addInstrumentFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addInstrumentFactoryInfo);
                    $('#addInstrumentFactoryModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/instrumentFactory.saveInstrumentFactory',
                    JSON.stringify($that.addInstrumentFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addInstrumentFactoryModel').modal('hide');
                            $that.clearAddInstrumentFactoryInfo();
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
            clearAddInstrumentFactoryInfo: function () {
                $that.addInstrumentFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
