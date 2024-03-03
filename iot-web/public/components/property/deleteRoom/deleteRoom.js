(function (vc) {
    vc.extends({
        data: {
            deleteRoomInfo: {}
        },
        _initEvent: function () {
            vc.on('deleteRoom', 'openRoomModel', function (_roomInfo) {
                $that.deleteRoomInfo = _roomInfo;
                $('#deleteRoomModel').modal('show');
            });
        },
        methods: {
            closeDeleteRoomModel: function () {
                $('#deleteRoomModel').modal('hide');
            },
            deleteRoom: function () {
                $that.deleteRoomInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/room.deleteRoom',
                    JSON.stringify($that.deleteRoomInfo), {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteRoomModel').modal('hide');
                            vc.emit('room', 'loadData', {
                                floorId: $that.deleteRoomInfo.floorId
                            });
                            vc.emit('shops', 'loadData', {
                                floorId: $that.deleteRoomInfo.floorId
                            });
                            vc.toast("删除成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    }
                );
            }
        }
    });
})(window.vc);