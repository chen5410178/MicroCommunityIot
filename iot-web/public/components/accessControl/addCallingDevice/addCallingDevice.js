(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addCallingDeviceInfo: {
                deviceName: '',
                callingNum: '',
                platformId: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addCallingDevice', 'openAddCallingDeviceModal', function () {
                $('#addCallingDeviceModel').modal('show');
            });
        },
        methods: {
            addCallingDeviceValidate() {
                return vc.validate.validate({
                    addCallingDeviceInfo: vc.component.addCallingDeviceInfo
                }, {
                    'addCallingDeviceInfo.deviceName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "设备名称不能超过30"
                        },
                    ],
                    'addCallingDeviceInfo.callingNum': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "呼叫号码不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "32",
                            errInfo: "呼叫号码不能为空"
                        },
                    ],
                    'addCallingDeviceInfo.remark': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "备注不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "描述不能为空"
                        },
                    ],


                });
            },
            saveCallingDeviceInfo: function () {
                if (!vc.component.addCallingDeviceValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.component.addCallingDeviceInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, vc.component.addCallingDeviceInfo);
                    $('#addCallingDeviceModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    'callingDevice.saveCallingDevice',
                    JSON.stringify(vc.component.addCallingDeviceInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCallingDeviceModel').modal('hide');
                            vc.component.clearAddCallingDeviceInfo();
                            vc.emit('callingDeviceManage', 'listCallingDevice', {});

                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddCallingDeviceInfo: function () {
                vc.component.addCallingDeviceInfo = {
                    deviceName: '',
                    callingNum: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
