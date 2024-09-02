(function (vc, vm) {

    vc.extends({
        data: {
            editCallingContactInfo: {
                deviceId: '',
                deviceId: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editCallingContact', 'openEditCallingContactModal', function (_params) {
                vc.component.refreshEditCallingContactInfo();
                $('#editCallingContactModel').modal('show');
                vc.copyObject(_params, vc.component.editCallingContactInfo);
                vc.component.editCallingContactInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editCallingContactValidate: function () {
                return vc.validate.validate({
                    editCallingContactInfo: vc.component.editCallingContactInfo
                }, {
                    'editCallingContactInfo.deviceId': [
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
                    'editCallingContactInfo.remark': [
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
                    'editCallingContactInfo.deviceId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "通讯录ID不能为空"
                        }]

                });
            },
            editCallingContact: function () {
                if (!vc.component.editCallingContactValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    'callingContact.updateCallingContact',
                    JSON.stringify(vc.component.editCallingContactInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editCallingContactModel').modal('hide');
                            vc.emit('callingContactManage', 'listCallingContact', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);
                    });
            },
            refreshEditCallingContactInfo: function () {
                vc.component.editCallingContactInfo = {
                    deviceId: '',
                    deviceId: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.vc.component);
