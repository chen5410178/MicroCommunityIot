/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailOwnerInfo: {
                owners: [],
                ownerId:'',
                roomNum: '',
                allOweFeeAmount:'0'
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailOwner', 'switch', function (_data) {
                $that.roomDetailOwnerInfo.roomId = _data.roomId;
                $that._loadRoomDetailOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailOwner', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailOwnerData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailOwner', 'notify', function (_data) {
                $that._loadRoomDetailOwnerData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailOwnerData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailOwnerInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/owner.queryOwners',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailOwnerInfo.owners = _roomInfo.data;
                        vc.emit('roomDetailOwner', 'paginationPlus', 'init', {
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
            _qureyRoomDetailOwner: function () {
                $that._loadRoomDetailOwnerData(DEFAULT_PAGE, DEFAULT_ROWS);
            },

            
            ownerExitRoomModel: function(_room) {
                vc.emit('ownerExitRoom', 'openExitRoomModel', {
                    ownerId:  $that.roomDetailOwnerInfo.ownerId,
                    roomId: _room.roomId
                });
            },
        }
    });
})(window.vc);