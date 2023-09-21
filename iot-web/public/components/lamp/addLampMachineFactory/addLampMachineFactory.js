(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLampMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLampMachineFactory', 'openAddLampMachineFactoryModal', function () {
                $('#addLampMachineFactoryModel').modal('show');
            });
        },
        methods: {
            addLampMachineFactoryValidate() {
                return vc.validate.validate({
                    addLampMachineFactoryInfo: $that.addLampMachineFactoryInfo
                }, {
                    'addLampMachineFactoryInfo.factoryName': [
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
                    'addLampMachineFactoryInfo.beanImpl': [
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
                    'addLampMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveLampMachineFactoryInfo: function () {
                if (!$that.addLampMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.addLampMachineFactoryInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addLampMachineFactoryInfo);
                    $('#addLampMachineFactoryModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/lampMachineFactory.saveLampMachineFactory',
                    JSON.stringify($that.addLampMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLampMachineFactoryModel').modal('hide');
                            $that.clearAddLampMachineFactoryInfo();
                            vc.emit('lampMachineFactoryManage', 'listLampMachineFactory', {});

                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLampMachineFactoryInfo: function () {
                $that.addLampMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);
