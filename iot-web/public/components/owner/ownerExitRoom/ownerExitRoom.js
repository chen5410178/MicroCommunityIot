(function(vc) {
    vc.extends({
        data: {
            exitRoomInfo: {}
        },
        _initEvent: function() {
            vc.on('ownerExitRoom', 'openExitRoomModel', function(_roomInfo) {
                $that.exitRoomInfo = _roomInfo;
                $('#exitRoomModel').modal('show');
            });
        },
        methods: {
            closeExitRoomModel: function() {
                $('#exitRoomModel').modal('hide');
            },
            doOwnerExitRoom: function() {

                $that.exitRoomInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/room.exitRoom',
                    JSON.stringify($that.exitRoomInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#exitRoomModel').modal('hide');
                            vc.emit('roomUnBindOwner', 'notify', $that.exitRoomInfo);

                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);
                    });
            }
        }
    });
})(window.vc);