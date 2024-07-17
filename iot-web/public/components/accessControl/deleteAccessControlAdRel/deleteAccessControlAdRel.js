(function(vc, vm) {

    vc.extends({
        data: {
            deleteAccessControlAdRelInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteAccessControlAdRel', 'openDeleteAccessControlAdRelModal', function(_params) {

                $that.deleteAccessControlAdRelInfo = _params;
                $('#deleteAccessControlAdRelModel').modal('show');

            });
        },
        methods: {
            deleteAccessControlAdRel: function() {
                $that.deleteAccessControlAdRelInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/ad.deleteAccessControlAdRel',
                    JSON.stringify($that.deleteAccessControlAdRelInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccessControlAdRelModel').modal('hide');
                            vc.emit('accessControlAdRel', 'listAccessControlAdRel', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteAccessControlAdRelModel: function() {
                $('#deleteAccessControlAdRelModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);