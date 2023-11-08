/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailLockLogInfo: {
                logs: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailLockLog', 'switch', function (_data) {
                $that.roomDetailLockLogInfo.roomId = _data.roomId;
                $that._loadRoomDetailLockLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailLockLog', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailLockLogData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailLockLog', 'notify', function (_data) {
                $that._loadRoomDetailLockLogData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailLockLogData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailLockLogInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockLog.listLockLog',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailLockLogInfo.logs = _roomInfo.data;
                        vc.emit('roomDetailLockLog', 'paginationPlus', 'init', {
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
            _qureyRoomDetailLockLog: function () {
                $that._loadRoomDetailLockLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openViewLockLogModel: function (_log) {
                vc.emit('showLockLog', 'openShowLockLogModal', _log);
            },
        }
    });
})(window.vc);