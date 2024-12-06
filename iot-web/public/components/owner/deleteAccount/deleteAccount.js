(function(vc, vm) {

    vc.extends({
        data: {
            deleteAccountInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteAccount', 'openDeleteAccountModal', function(_params) {

                $that.deleteAccountInfo = _params;
                $('#deleteAccountModel').modal('show');

            });
        },
        methods: {
            deleteAccount: function() {
                vc.http.apiPost(
                    '/account.deleteAccount',
                    JSON.stringify($that.deleteAccountInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccountModel').modal('hide');
                            vc.emit('account', 'listAccount', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteAccountBondModel: function() {
                $('#deleteAccountModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);