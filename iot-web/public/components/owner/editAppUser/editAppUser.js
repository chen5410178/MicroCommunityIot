(function (vc, vm) {
    vc.extends({
        data: {
            editAppUserInfo: {

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editAppUser', 'openEditAppUserModal', function (_params) {
                $that.editAppUserInfo = _params;
                $('#editAppUserModel').modal('show');
            });
        },
        methods: {
            editAppUser: function (flag) {
                switch (flag) {
                    case 'Y':
                        $that.editAppUserInfo.state = '12000';
                        break;
                    case 'N':
                        $that.editAppUserInfo.state = '13000';
                        $that.editAppUserInfo.stateMsg = '失败';
                        break;
                    default:
                        break;
                }
                vc.http.apiPost(
                    '/appUser.updateAppUser',
                    JSON.stringify($that.editAppUserInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editAppUserModel').modal('hide');
                            vc.emit('appUser', 'listAppUser', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
        }
    });

})(window.vc, window.$that);
