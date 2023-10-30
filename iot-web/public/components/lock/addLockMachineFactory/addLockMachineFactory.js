(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addLockMachineFactoryInfo: {
                factoryId: '',
                factoryName: '',
                beanImpl: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addLockMachineFactory', 'openAddLockMachineFactoryModal', function () {
                $('#addLockMachineFactoryModel').modal('show');
            });
        },
        methods: {
            addLockMachineFactoryValidate() {
                return vc.validate.validate({
                    addLockMachineFactoryInfo: $that.addLockMachineFactoryInfo
                }, {
                    'addLockMachineFactoryInfo.factoryName': [
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
                    'addLockMachineFactoryInfo.beanImpl': [
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
                    'addLockMachineFactoryInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                });
            },
            saveLockMachineFactoryInfo: function () {
                if (!$that.addLockMachineFactoryValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, $that.addLockMachineFactoryInfo);
                    $('#addLockMachineFactoryModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    '/lockMachineFactory.saveLockMachineFactory',
                    JSON.stringify($that.addLockMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#addLockMachineFactoryModel').modal('hide');
                            $that.clearAddLockMachineFactoryInfo();
                            vc.emit('lockMachineFactoryManage', 'listLockMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAddLockMachineFactoryInfo: function () {
                $that.addLockMachineFactoryInfo = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
