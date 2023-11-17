(function (vc, vm) {

    vc.extends({
        data: {
            authAppUserInfo: {
                auId: '',
                ownerName: '',
                link: '',
                roomName: '',
                openId: '',
                state: '',
                stateMsg: '',
                remark: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('authAppUser', 'openEditAppUserModal', function (_params) {
                $that.refreshEditAppUserInfo();
                $('#authAppUserModel').modal('show');
                vc.copyObject(_params, $that.authAppUserInfo);
                $that.authAppUserInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            authAppUserValidate: function () {
                return vc.validate.validate({
                    authAppUserInfo: $that.authAppUserInfo
                }, {
                    'authAppUserInfo.state': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "状态类型不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "状态类型不能超过12"
                        },
                    ],
                    'authAppUserInfo.stateMsg': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "审核说明不能超过512"
                        },
                    ],
                    'authAppUserInfo.auId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]

                });
            },
            _authAppUser: function () {
                if (!$that.authAppUserValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/appUser.updateAppUser',
                    JSON.stringify($that.authAppUserInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#authAppUserModel').modal('hide');
                            vc.emit('appUser', 'listAppUser', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            },
            refreshEditAppUserInfo: function () {
                $that.authAppUserInfo = {
                    auId: '',
                    ownerName: '',
                    link: '',
                    roomName: '',
                    openId: '',
                    state: '',
                    stateMsg: '',
                    remark: '',
                }
            }
        }
    });

})(window.vc, window.$that);
