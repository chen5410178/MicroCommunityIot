(function(vc) {

    vc.extends({
        data: {
            roomUnBindOwnerInfo: {
                roomId: '',
                roomName: '',
                owners: [],
            }
        },
        _initMethod: function() {
            $that.roomUnBindOwnerInfo.roomId = vc.getParam('roomId');
            $that.roomUnBindOwnerInfo.roomName = vc.getParam('roomName');
            $that._loadRoomOwners();
        },
        _initEvent: function() {
            vc.on('roomUnBindOwner', 'notify', function(_owner) {
                $that._loadRoomOwners();
            });
        },
        methods: {
            _loadRoomOwners: function() {
                let _param = {
                    params: {
                        page: 1,
                        row: 50,
                        roomId: $that.roomUnBindOwnerInfo.roomId,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                }

                vc.http.apiGet(
                    '/room.queryRoomOwner',
                    _param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code != 0) {
                            //关闭model
                            vc.toast('未包含人员');
                            return;
                        }

                        $that.roomUnBindOwnerInfo.owners = _json.data;
                    },
                    function(errInfo, error) {

                    });
            },
            _goBack: function() {
                vc.goBack();
            },
            ownerExitRoomModel: function(_owner) {
                vc.emit('ownerExitRoom', 'openExitRoomModel', {
                    roomId: $that.roomUnBindOwnerInfo.roomId,
                    ownerId: _owner.memberId
                });
            },
        }
    });

})(window.vc);