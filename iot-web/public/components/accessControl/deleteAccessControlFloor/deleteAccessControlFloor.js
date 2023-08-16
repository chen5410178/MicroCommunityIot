(function(vc, vm) {

    vc.extends({
        data: {
            deleteAccessControlFloorInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteAccessControlFloor', 'openDeleteAccessControlFloorModal', function(_params) {

                vc.component.deleteAccessControlFloorInfo = _params;
                $('#deleteAccessControlFloorModel').modal('show');

            });
        },
        methods: {
            deleteAccessControlFloor: function() {
                vc.component.deleteAccessControlFloorInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/accessControlFloor.deleteAccessControlFloor',
                    JSON.stringify(vc.component.deleteAccessControlFloorInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAccessControlFloorModel').modal('hide');
                            vc.emit('accessControlFloor', 'listAccessControlFloor', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(json);

                    });
            },
            closeDeleteAccessControlFloorModel: function() {
                $('#deleteAccessControlFloorModel').modal('hide');
            }
        }
    });

})(window.vc, window.vc.component);