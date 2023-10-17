(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMonitorModelInfo: {
                modelId: '',
                modelName: '',
                modelAdapt: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMonitorModel', 'openAddMonitorModelModal', function () {
                $('#addMonitorModelModel').modal('show');
            });
        },
        methods: {
            addMonitorModelValidate() {
                return vc.validate.validate({
                    addMonitorModelInfo: $that.addMonitorModelInfo
                }, {
                    'addMonitorModelInfo.modelName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "模型名称不能超过200"
                        },
                    ],
                    'addMonitorModelInfo.modelAdapt': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模型处理类不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "模型处理类不能超过64"
                        },
                    ],
                });
            },
            saveMonitorModelInfo: function () {
                if (!$that.addMonitorModelValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addMonitorModelInfo);
                    $('#addMonitorModelModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/monitorModel.saveMonitorModel',
                    JSON.stringify($that.addMonitorModelInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addMonitorModelModel').modal('hide');
                            $that.clearAddMonitorModelInfo();
                            vc.emit('monitorModelManage', 'listMonitorModel', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddMonitorModelInfo: function () {
                $that.addMonitorModelInfo = {
                    modelId: '',
                    modelName: '',
                    modelAdapt: '',
                };
            }
        }
    });
})(window.vc);
