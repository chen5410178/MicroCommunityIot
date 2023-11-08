/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            roomDetailInfo: {
                viewRoomFlag: '',
                roomId: "",
                floorNum: '',
                unitNum: "",
                roomNum: "",
                layer: "",
                roomSubTypeName: "",
                builtUpArea: "",
                roomArea: "",
                stateName: "",
                startTime: "",
                roomAttrDtos: [],
                url: '',
                _currentTab: 'roomDetailOwner',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.roomDetailInfo.roomId = vc.getParam('roomId');
            $that.roomDetailInfo.needBack = vc.getParam('needBack');
            if (!vc.notNull($that.roomDetailInfo.roomId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.roomDetailInfo._currentTab = _currentTab;
            }

            $that._loadRoomInfo();
            $that.changeTab($that.roomDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.roomDetailInfo._currentTab);
            });
        },
        methods: {
            _loadRoomInfo: function() {
                let param = {
                        params: {
                            roomId: $that.roomDetailInfo.roomId,
                            page: 1,
                            row: 1,
                            communityId: vc.getCurrentCommunity().communityId,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/room.queryRooms',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.roomDetailInfo);
                        $that.roomDetailInfo.roomAttrDtos = _json.data[0].roomAttrDtos
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.roomDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    roomId: $that.roomDetailInfo.roomId,
                    roomName: $that.roomDetailInfo.name,
                    link: $that.roomDetailInfo.link,
                })
            },
            errorLoadImg: function() {
                $that.roomDetailInfo.RoomPhoto = "/img/noPhoto.jpg";
            },
            _openEditroomDetailModel: function() {
                vc.emit('editRoom', 'openEditRoomModal', $that.roomDetailInfo);
            },
        }
    });
})(window.vc);