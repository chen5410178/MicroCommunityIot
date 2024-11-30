(function (vc, vm) {

    vc.extends({
        data: {
            editCallingPlatformInfo: {
                platformId: '',
                platformName: '',
                ipAddr: '',
                port: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editCallingPlatform', 'openEditCallingPlatformModal', function (_params) {
                $that.refreshEditCallingPlatformInfo();
                $('#editCallingPlatformModel').modal('show');
                vc.copyObject(_params, $that.editCallingPlatformInfo);
                $that.editCallingPlatformInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editCallingPlatformValidate: function () {
                return vc.validate.validate({
                    editCallingPlatformInfo: $that.editCallingPlatformInfo
                }, {
                    'editCallingPlatformInfo.platformName': [
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
                    'editCallingPlatformInfo.ipAddr': [
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
                    'editCallingPlatformInfo.remark': [
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
                    'editCallingPlatformInfo.platformId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "平台ID不能为空"
                        }]

                });
            },
            editCallingPlatform: function () {
                if (!$that.editCallingPlatformValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/callingPlatform.updateCallingPlatform',
                    JSON.stringify($that.editCallingPlatformInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editCallingPlatformModel').modal('hide');
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
            refreshEditCallingPlatformInfo: function () {
                $that.editCallingPlatformInfo = {
                    platformId: '',
                    platformName: '',
                    ipAddr: '',
                    port: '',
                    remark: '',

                }
            }
        }
    });

})(window.vc, window.$that);
