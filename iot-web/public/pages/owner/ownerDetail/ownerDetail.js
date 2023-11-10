/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            ownerDetailInfo: {
                viewOwnerFlag: '',
                memberId:'',
                ownerId: "",
                name: '',
                link: "",
                sex: "",
                idCard: "",
                address: "",
                ownerTypeName: "",
                ownerAttrDtos: [],
                url: '',
                _currentTab: 'ownerDetailRoom',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.ownerDetailInfo.memberId = vc.getParam('memberId');
            $that.ownerDetailInfo.ownerId = vc.getParam('ownerId');
            $that.ownerDetailInfo.needBack = vc.getParam('needBack');
            if (!vc.notNull($that.ownerDetailInfo.memberId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.ownerDetailInfo._currentTab = _currentTab;
            }

            $that._loadOwnerInfo();
            $that.changeTab($that.ownerDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('ownerDetail', 'listRoomData', function(_info) {
                $that._loadOwnerInfo();
                $that.changeTab($that.ownerDetailInfo._currentTab);
            });
        },
        methods: {
            _loadOwnerInfo: function() {
                let param = {
                        params: {
                            memberId: $that.ownerDetailInfo.memberId,
                            page: 1,
                            row: 1,
                            communityId: vc.getCurrentCommunity().communityId,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/owner.queryOwners',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.ownerDetailInfo);
                        $that.ownerDetailInfo.ownerAttrDtos = _json.data[0].ownerAttrDtos
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
                    memberId: $that.ownerDetailInfo.memberId,
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