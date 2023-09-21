(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLampMachineFactorySpecInfo: {
                specId: '',
                factoryId: '',
                specName: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLampMachineFactorySpec', 'openAddLampMachineFactorySpecModal', function (_factoryId) {
                $that.addLampMachineFactorySpecInfo.factoryId = _factoryId;
                $('#addLampMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            addLampMachineFactorySpecValidate() {
                return vc.validate.validate({
                    addLampMachineFactorySpecInfo: $that.addLampMachineFactorySpecInfo
                }, {
                    'addLampMachineFactorySpecInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家ID不能为空"
                        }
                    ],
                    'addLampMachineFactorySpecInfo.specName': [
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
                    'addLampMachineFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            saveLampMachineFactorySpecInfo: function () {
                if (!$that.addLampMachineFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addLampMachineFactorySpecInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addLampMachineFactorySpecInfo);
                    $('#addLampMachineFactorySpecModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/lampMachineFactorySpec.saveLampMachineFactorySpec',
                    JSON.stringify($that.addLampMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLampMachineFactorySpecModel').modal('hide');
                            $that.clearAddLampMachineFactorySpecInfo();
                            vc.emit('lampMachineFactorySpecManage', 'listLampMachineFactorySpec', {});

                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLampMachineFactorySpecInfo: function () {
                $that.addLampMachineFactorySpecInfo = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                    remark: '',
                };
            }
        }
    });

})(window.vc);
