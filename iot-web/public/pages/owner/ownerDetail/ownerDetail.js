/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            ownerDetailInfo: {
                viewRoomFlag: '',
                ownerId: "",
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
                _currentTab: 'ownerDetailOwner',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.ownerDetailInfo.ownerId = vc.getParam('ownerId');
            $that.ownerDetailInfo.needBack = vc.getParam('needBack');
            if (!vc.notNull($that.ownerDetailInfo.ownerId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.ownerDetailInfo._currentTab = _currentTab;
            }

            $that._loadRoomInfo();
            $that.changeTab($that.ownerDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('ownerDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.ownerDetailInfo._currentTab);
            });
        },
        methods: {
            _loadOwnerInfo: function() {
                let param = {
                        params: {
                            ownerId: $that.ownerDetailInfo.ownerId,
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
                        vc.copyObject(_json.data[0], $that.ownerDetailInfo);
                        $that.ownerDetailInfo.roomAttrDtos = _json.data[0].roomAttrDtos
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.ownerDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    ownerId: $that.ownerDetailInfo.ownerId,
                    ownerName: $that.ownerDetailInfo.name,
                    link: $that.ownerDetailInfo.link,
                })
            },
            errorLoadImg: function() {
                $that.ownerDetailInfo.RoomPhoto = "/img/noPhoto.jpg";
            },
            _openEditOwnerDetailModel: function() {
                vc.emit('editRoom', 'openEditRoomModal', $that.ownerDetailInfo);
            },
        }
    });
})(window.vc);