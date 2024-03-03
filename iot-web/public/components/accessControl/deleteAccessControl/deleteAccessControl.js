(function(vc, vm) {

    vc.extends({
        data: {
            deleteAccessControlInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteAccessControl', 'openDeleteAccessControlModal', function(_params) {

                $that.deleteAccessControlInfo = _params;
                $('#deleteAccessControlModel').modal('show');

            });
        },
        methods: {
            deleteAccessControl: function() {
                $that.deleteAccessControlInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/accessControl.deleteAccessControl',
                    JSON.stringify($that.deleteAccessControlInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccessControlModel').modal('hide');
                            vc.emit('accessControlManage', 'listAccessControl', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(json);

                    });
            },
            closeDeleteAccessControlModel: function() {
                $('#deleteAccessControlModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);