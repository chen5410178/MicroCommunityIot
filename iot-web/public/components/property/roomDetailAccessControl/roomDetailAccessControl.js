/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailAccessControlInfo: {
                machines: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailAccessControl', 'switch', function (_data) {
                $that.roomDetailAccessControlInfo.roomId = _data.roomId;
                $that._loadRoomDetailAccessControlData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailAccessControl', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailAccessControlData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailAccessControl', 'notify', function (_data) {
                $that._loadRoomDetailAccessControlData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailAccessControlData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailAccessControlInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/accessControlFloor.listAccessControlFloor',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailAccessControlInfo.machines = _roomInfo.data;
                        vc.emit('roomDetailAccessControl', 'paginationPlus', 'init', {
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
            _qureyRoomDetailAccessControl: function () {
                $that._loadRoomDetailAccessControlData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);