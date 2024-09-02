(function (vc, vm) {

    vc.extends({
        data: {
            editCallingDeviceInfo: {
                deviceId: '',
                deviceName: '',
                platformId: '',
                callingNum: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editCallingDevice', 'openEditCallingDeviceModal', function (_params) {
                vc.component.refreshEditCallingDeviceInfo();
                $('#editCallingDeviceModel').modal('show');
                vc.copyObject(_params, vc.component.editCallingDeviceInfo);
                vc.component.editCallingDeviceInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editCallingDeviceValidate: function () {
                return vc.validate.validate({
                    editCallingDeviceInfo: vc.component.editCallingDeviceInfo
                }, {
                    'editCallingDeviceInfo.deviceName': [
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
                    'editCallingDeviceInfo.callingNum': [
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
                    'editCallingDeviceInfo.remark': [
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
                    'editCallingDeviceInfo.deviceId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        }]

                });
            },
            editCallingDevice: function () {
                if (!vc.component.editCallingDeviceValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'callingDevice.updateCallingDevice',
                    JSON.stringify(vc.component.editCallingDeviceInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editCallingDeviceModel').modal('hide');
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
            refreshEditCallingDeviceInfo: function () {
                vc.component.editCallingDeviceInfo = {
                    deviceId: '',
                    deviceName: '',
                    platformId: '',
                    callingNum: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.vc.component);
