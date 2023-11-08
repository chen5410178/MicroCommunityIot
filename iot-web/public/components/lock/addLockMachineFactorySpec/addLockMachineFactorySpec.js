(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLockMachineFactorySpecInfo: {
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
            vc.on('addLockMachineFactorySpec', 'openAddLockMachineFactorySpecModal', function (_factoryId) {
                if (!_factoryId) {
                    vc.toast("缺少厂家信息");
                    return;
                }
                $that.addLockMachineFactorySpecInfo.factoryId = _factoryId;
                $('#addLockMachineFactorySpecModel').modal('show');
            });
        },
        methods: {
            addLockMachineFactorySpecValidate() {
                return vc.validate.validate({
                    addLockMachineFactorySpecInfo: $that.addLockMachineFactorySpecInfo
                }, {
                    'addLockMachineFactorySpecInfo.factoryId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家名称不能为空"
                        },
                    ],
                    'addLockMachineFactorySpecInfo.specName': [
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
                    'addLockMachineFactorySpecInfo.specCd': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "规格编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "64",
                            errInfo: "规格编号不能超过64"
                        },
                    ],
                    'addLockMachineFactorySpecInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "256",
                            errInfo: "描述不能超过256"
                        },
                    ],
                });
            },
            saveLockMachineFactorySpecInfo: function () {
                if (!$that.addLockMachineFactorySpecValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addLockMachineFactorySpecInfo);
                    $('#addLockMachineFactorySpecModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/lockMachineFactorySpec.saveLockMachineFactorySpec',
                    JSON.stringify($that.addLockMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLockMachineFactorySpecModel').modal('hide');
                            $that.clearAddLockMachineFactorySpecInfo();
                            vc.emit('lockMachineFactorySpecManage', 'listLockMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLockMachineFactorySpecInfo: function () {
                $that.addLockMachineFactorySpecInfo = {
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
