/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailMemberInfo: {
                owners: [],
                ownerId:'',
                roomNum: '',
                allOweFeeAmount:'0'
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailMember', 'switch', function (_data) {
                $that.ownerDetailMemberInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailMemberData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailMember', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailMemberData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailMember', 'notify', function (_data) {
                $that._loadOwnerDetailMemberData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailMemberData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ownerId:$that.ownerDetailMemberInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/owner.queryOwners',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailMemberInfo.owners = _roomInfo.data;
                        vc.emit('ownerDetailMember', 'paginationPlus', 'init', {
                            total: _roomInfo.records,
                            dataCount: _roomInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _qureyOwnerDetailMember: function () {
                $that._loadOwnerDetailMemberData(DEFAULT_PAGE, DEFAULT_ROWS);
            },

            
            ownerExitRoomModel: function(_room) {
                vc.emit('ownerExitRoom', 'openExitRoomModel', {
                    ownerId:  $that.ownerDetailMemberInfo.ownerId,
                    roomId: _room.roomId
                });
            },
        }
    });
})(window.vc);