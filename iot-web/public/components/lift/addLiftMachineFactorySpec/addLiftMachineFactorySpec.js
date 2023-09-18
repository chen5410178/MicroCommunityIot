(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLiftMachineFactorySpecInfo: {
                specId: '',
                factoryId: '',
                specName: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLiftMachineFactorySpec', 'openAddLiftMachineFactorySpecModal', function (_factoryId) {
                $that.addLiftMachineFactorySpecInfo.factoryId = _factoryId;
                $('#addLiftMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            addLiftMachineFactorySpecValidate() {
                return vc.validate.validate({
                    addLiftMachineFactorySpecInfo: vc.component.addLiftMachineFactorySpecInfo
                }, {
                    'addLiftMachineFactorySpecInfo.factoryId': [
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
                    'addLiftMachineFactorySpecInfo.specName': [
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
                    'addLiftMachineFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            saveLiftMachineFactorySpecInfo: function () {
                if (!vc.component.addLiftMachineFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.component.addLiftMachineFactorySpecInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, vc.component.addLiftMachineFactorySpecInfo);
                    $('#addLiftMachineFactorySpecModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    'liftMachineFactorySpec.saveLiftMachineFactorySpec',
                    JSON.stringify(vc.component.addLiftMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addLiftMachineFactorySpecModel').modal('hide');
                            vc.component.clearAddLiftMachineFactorySpecInfo();
                            vc.emit('liftMachineFactorySpecManage', 'listLiftMachineFactorySpec', {});

                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddLiftMachineFactorySpecInfo: function () {
                vc.component.addLiftMachineFactorySpecInfo = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);
