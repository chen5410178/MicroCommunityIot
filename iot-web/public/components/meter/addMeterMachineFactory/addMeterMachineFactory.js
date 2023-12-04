(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addMeterMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                machineModel: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addMeterMachineFactory', 'openAddMeterMachineFactoryModal', function () {
                $('#addMeterMachineFactoryModel').modal('show');
            });
        },
        methods: {
            addMeterMachineFactoryValidate() {
                return vc.validate.validate({
                    addMeterMachineFactoryInfo: $that.addMeterMachineFactoryInfo
                }, {
                    'addMeterMachineFactoryInfo.factoryName': [
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
                    'addMeterMachineFactoryInfo.beanImpl': [
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
                    'addMeterMachineFactoryInfo.machineModel': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "模式不能为空"
                        },
                    ],
                    'addMeterMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveMeterMachineFactoryInfo: function () {
                $that.addMeterMachineFactoryInfo.communityId = vc.getCurrentCommunity().communityId;
                if (!$that.addMeterMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addMeterMachineFactoryInfo);
                    $('#addMeterMachineFactoryModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/meterMachineFactory.saveMeterMachineFactory',
                    JSON.stringify($that.addMeterMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addMeterMachineFactoryModel').modal('hide');
                            $that.clearAddMeterMachineFactoryInfo();
                            vc.emit('meterMachineFactoryManage', 'listMeterMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddMeterMachineFactoryInfo: function () {
                $that.addMeterMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    machineModel: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
