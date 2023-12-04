(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMeterMachineFactorySpecInfo: {
                specId: '',
                factoryId: '',
                specName: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMeterMachineFactorySpec', 'openAddMeterMachineFactorySpecModal', function (_factoryId) {
                $that.addMeterMachineFactorySpecInfo.factoryId = _factoryId;
                $('#addMeterMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            addMeterMachineFactorySpecValidate() {
                return vc.validate.validate({
                    addMeterMachineFactorySpecInfo: $that.addMeterMachineFactorySpecInfo
                }, {
                    'addMeterMachineFactorySpecInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        }, ,
                    ],
                    'addMeterMachineFactorySpecInfo.specName': [
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
                    'addMeterMachineFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            saveMeterMachineFactorySpecInfo: function () {
                if (!$that.addMeterMachineFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addMeterMachineFactorySpecInfo);
                    $('#addMeterMachineFactorySpecModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/meterMachineFactorySpec.saveMeterMachineFactorySpec',
                    JSON.stringify($that.addMeterMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addMeterMachineFactorySpecModel').modal('hide');
                            $that.clearAddMeterMachineFactorySpecInfo();
                            vc.emit('meterMachineFactorySpecManage', 'listMeterMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddMeterMachineFactorySpecInfo: function () {
                $that.addMeterMachineFactorySpecInfo = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
