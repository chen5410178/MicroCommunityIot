(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addChargeMachineFactorySpecInfo: {
                specId: '',
                factoryId: '',
                specName: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addChargeMachineFactorySpec', 'openAddChargeMachineFactorySpecModal', function (_factoryId) {
                $that.addChargeMachineFactorySpecInfo.factoryId = _factoryId;
                $('#addChargeMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            addChargeMachineFactorySpecValidate() {
                return vc.validate.validate({
                    addChargeMachineFactorySpecInfo: $that.addChargeMachineFactorySpecInfo
                }, {
                    'addChargeMachineFactorySpecInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        },
                    ],
                    'addChargeMachineFactorySpecInfo.specName': [
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
                    'addChargeMachineFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            saveChargeMachineFactorySpecInfo: function () {
                if (!$that.addChargeMachineFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addChargeMachineFactorySpecInfo);
                    $('#addChargeMachineFactorySpecModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/chargeMachineFactorySpec.saveChargeMachineFactorySpec',
                    JSON.stringify($that.addChargeMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addChargeMachineFactorySpecModel').modal('hide');
                            $that.clearAddChargeMachineFactorySpecInfo();
                            vc.emit('chargeMachineFactorySpecManage', 'listChargeMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddChargeMachineFactorySpecInfo: function () {
                $that.addChargeMachineFactorySpecInfo = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
