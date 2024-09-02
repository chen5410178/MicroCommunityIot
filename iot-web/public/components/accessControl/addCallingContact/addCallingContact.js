(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addCallingContactInfo: {
                deviceId: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addCallingContact', 'openAddCallingContactModal', function () {
                $('#addCallingContactModel').modal('show');
            });
        },
        methods: {
            addCallingContactValidate() {
                return vc.validate.validate({
                    addCallingContactInfo: vc.component.addCallingContactInfo
                }, {
                    'addCallingContactInfo.deviceId': [
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
                    'addCallingContactInfo.remark': [
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
            saveCallingContactInfo: function () {
                if (!vc.component.addCallingContactValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.component.addCallingContactInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                if (vc.notNull($props.callBackListener)) {
                    vc.emit($props.callBackListener, $props.callBackFunction, vc.component.addCallingContactInfo);
                    $('#addCallingContactModel').modal('hide');
                    return;
                }

                vc.http.apiPost(
                    'callingContact.saveCallingContact',
                    JSON.stringify(vc.component.addCallingContactInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCallingContactModel').modal('hide');
                            vc.component.clearAddCallingContactInfo();
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
            clearAddCallingContactInfo: function () {
                vc.component.addCallingContactInfo = {
                    deviceId: '',
                    remark: '',

                };
            }
        }
    });

})(window.vc);
