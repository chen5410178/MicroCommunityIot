(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addInstrumentFactorySpecInfo: {
                specId: '',
                factoryId: '',
                specName: '',
                specCd: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addInstrumentFactorySpec', 'openAddInstrumentFactorySpecModal', function (_factoryId) {
                $that.addInstrumentFactorySpecInfo.factoryId = _factoryId;
                $('#addInstrumentFactorySpecModel').modal('show');
            });
        },
        methods: {
            addInstrumentFactorySpecValidate() {
                return vc.validate.validate({
                    addInstrumentFactorySpecInfo: $that.addInstrumentFactorySpecInfo
                }, {
                    'addInstrumentFactorySpecInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        },
                    ],
                    'addInstrumentFactorySpecInfo.specName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "规格名称不能超过64"
                        },
                    ],
                    'addInstrumentFactorySpecInfo.specCd': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "规格名称不能超过12"
                        },
                    ],
                    'addInstrumentFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            saveInstrumentFactorySpecInfo: function () {
                if (!$that.addInstrumentFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addInstrumentFactorySpecInfo);
                    $('#addInstrumentFactorySpecModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/instrumentFactorySpec.saveInstrumentFactorySpec',
                    JSON.stringify($that.addInstrumentFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addInstrumentFactorySpecModel').modal('hide');
                            $that.clearAddInstrumentFactorySpecInfo();
                            vc.emit('instrumentFactorySpecManage', 'listInstrumentFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddInstrumentFactorySpecInfo: function () {
                $that.addInstrumentFactorySpecInfo = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                    specCd: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
