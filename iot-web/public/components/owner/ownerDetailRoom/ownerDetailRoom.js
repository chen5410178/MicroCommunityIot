/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailRoomInfo: {
                rooms: [],
                ownerId:'',
                roomNum: '',
                allOweFeeAmount:'0'
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailRoom', 'switch', function (_data) {
                $that.ownerDetailRoomInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailRoomData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailRoom', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailRoomData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailRoom', 'notify', function (_data) {
                $that._loadOwnerDetailRoomData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailRoomData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ownerId:$that.ownerDetailRoomInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/room.queryRoomsByOwner',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailRoomInfo.rooms = _roomInfo.data;
                        vc.emit('ownerDetailRoom', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailRoom: function () {
                $that._loadOwnerDetailRoomData(DEFAULT_PAGE, DEFAULT_ROWS);
            },

            _toRoomDetail:function(_room){
                vc.jumpToPage('/#/pages/property/roomDetail?roomId=' + _room.roomId);
            },

            
            ownerExitRoomModel: function(_room) {
                vc.emit('ownerExitRoom', 'openExitRoomModel', {
                    ownerId:  $that.ownerDetailRoomInfo.ownerId,
                    roomId: _room.roomId
                });
            },
        }
    });
})(window.vc);