/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailAccessControlLogInfo: {
                logs: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailAccessControlLog', 'switch', function (_data) {
                $that.roomDetailAccessControlLogInfo.roomId = _data.roomId;
                $that._loadRoomDetailAccessControlLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailAccessControlLog', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailAccessControlLogData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailAccessControlLog', 'notify', function (_data) {
                $that._loadRoomDetailAccessControlLogData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailAccessControlLogData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailAccessControlLogInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/accessControlLog.listAccessControlLog',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailAccessControlLogInfo.logs = _roomInfo.data;
                        vc.emit('roomDetailAccessControlLog', 'paginationPlus', 'init', {
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
            _qureyRoomDetailAccessControlLog: function () {
                $that._loadRoomDetailAccessControlLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openViewAccessControlLogModel: function(_log) {
                vc.emit('showAccessControlLog', 'openShowAccessControlLogModal', _log);
            },
        }
    });
})(window.vc);