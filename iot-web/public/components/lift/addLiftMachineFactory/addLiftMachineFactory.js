(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLiftMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLiftMachineFactory', 'openAddLiftMachineFactoryModal', function () {
                $('#addLiftMachineFactoryModel').modal('show');
            });
        },
        methods: {
            addLiftMachineFactoryValidate() {
                return vc.validate.validate({
                    addLiftMachineFactoryInfo: vc.component.addLiftMachineFactoryInfo
                }, {
                    'addLiftMachineFactoryInfo.factoryName': [
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
                    'addLiftMachineFactoryInfo.beanImpl': [
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
                    'addLiftMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveLiftMachineFactoryInfo: function () {
                if (!vc.component.addLiftMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.component.addLiftMachineFactoryInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, vc.component.addLiftMachineFactoryInfo);
                    $('#addLiftMachineFactoryModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    'liftMachineFactory.saveLiftMachineFactory',
                    JSON.stringify(vc.component.addLiftMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLiftMachineFactoryModel').modal('hide');
                            vc.component.clearAddLiftMachineFactoryInfo();
                            vc.emit('liftMachineFactoryManage', 'listLiftMachineFactory', {});

                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddLiftMachineFactoryInfo: function () {
                vc.component.addLiftMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);
