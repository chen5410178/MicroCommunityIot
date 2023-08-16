(function(vc, vm) {

    vc.extends({
        data: {
            deleteAccessControlOrgInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteAccessControlOrg', 'openDeleteAccessControlOrgModal', function(_params) {

                vc.component.deleteAccessControlOrgInfo = _params;
                $('#deleteAccessControlOrgModel').modal('show');

            });
        },
        methods: {
            deleteAccessControlOrg: function() {
                vc.component.deleteAccessControlOrgInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/accessControlOrg.deleteAccessControlOrg',
                    JSON.stringify(vc.component.deleteAccessControlOrgInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccessControlOrgModel').modal('hide');
                            vc.emit('accessControlOrg', 'listAccessControlOrg', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteAccessControlOrgModel: function() {
                $('#deleteAccessControlOrgModel').modal('hide');
            }
        }
    });

})(window.vc, window.vc.component);