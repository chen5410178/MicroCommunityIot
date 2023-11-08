/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailLockInfo: {
                lockMachines: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailLock', 'switch', function (_data) {
                $that.roomDetailLockInfo.roomId = _data.roomId;
                $that._loadRoomDetailLockData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailLock', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailLockData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailLock', 'notify', function (_data) {
                $that._loadRoomDetailLockData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailLockData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailLockInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockMachine.listLockMachine',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailLockInfo.lockMachines = _roomInfo.data;
                        vc.emit('roomDetailLock', 'paginationPlus', 'init', {
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
            _qureyRoomDetailLock: function () {
                $that._loadRoomDetailLockData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);