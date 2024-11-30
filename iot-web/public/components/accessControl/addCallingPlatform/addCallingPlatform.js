(function (vc) {

    vc.extends({
        data: {
            addCallingPlatformInfo: {
                platformName: '',
                ipAddr: '',
                port: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addCallingPlatform', 'openAddCallingPlatformModal', function () {
                $('#addCallingPlatformModel').modal('show');
            });
        },
        methods: {
            addCallingPlatformValidate() {
                return vc.validate.validate({
                    addCallingPlatformInfo: $that.addCallingPlatformInfo
                }, {
                    'addCallingPlatformInfo.platformName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "平台名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "平台名称不能超过30"
                        },
                    ],
                    'addCallingPlatformInfo.ipAddr': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "平台IP地址不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "32",
                            errInfo: "平台IP地址不能为空"
                        },
                    ],
                    'addCallingPlatformInfo.remark': [
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
            saveCallingPlatformInfo: function () {
                if (!$that.addCallingPlatformValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addCallingPlatformInfo.communityId = vc.getCurrentCommunity().communityId;
                //不提交数据将数据 回调给侦听处理
                vc.http.apiPost(
                    '/callingPlatform.saveCallingPlatform',
                    JSON.stringify($that.addCallingPlatformInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addCallingPlatformModel').modal('hide');
                            $that.clearAddCallingPlatformInfo();
                            vc.emit('callingPlatformManage', 'listCallingPlatform', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddCallingPlatformInfo: function () {
                $that.addCallingPlatformInfo = {
                    platformName: '',
                    ipAddr: '',
                    remark: '',
                    port: '',
                };
            }
        }
    });

})(window.vc);
