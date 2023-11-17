(function (vc, vm) {
    vc.extends({
        data: {
            deleteAppUserInfo: {

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteAppUser', 'openDeleteAppUserModal', function (_params) {
                $that.deleteAppUserInfo = _params;
                $('#deleteAppUserModel').modal('show');
            });
        },
        methods: {
            deleteAppUser: function () {
                $that.deleteAppUserInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/appUser.deleteAppUser',
                    JSON.stringify($that.deleteAppUserInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAppUserModel').modal('hide');
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
            closeDeleteAppUserModel: function () {
                $('#deleteAppUserModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
