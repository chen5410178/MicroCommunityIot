(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addChargeMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addChargeMachineFactory', 'openAddChargeMachineFactoryModal', function () {
                $('#addChargeMachineFactoryModel').modal('show');
            });
        },
        methods: {
            addChargeMachineFactoryValidate() {
                return vc.validate.validate({
                    addChargeMachineFactoryInfo: $that.addChargeMachineFactoryInfo
                }, {
                    'addChargeMachineFactoryInfo.factoryName': [
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
                    'addChargeMachineFactoryInfo.beanImpl': [
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
                    'addChargeMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveChargeMachineFactoryInfo: function () {
                if (!$that.addChargeMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addChargeMachineFactoryInfo);
                    $('#addChargeMachineFactoryModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/chargeMachine.saveChargeMachineFactory',
                    JSON.stringify($that.addChargeMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addChargeMachineFactoryModel').modal('hide');
                            $that.clearAddChargeMachineFactoryInfo();
                            vc.emit('chargeMachineFactoryManage', 'listChargeMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddChargeMachineFactoryInfo: function () {
                $that.addChargeMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
