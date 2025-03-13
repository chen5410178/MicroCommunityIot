/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminMeterDetailRoomInfo: {
                rooms: [],
                roomId:'',
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminMeterDetailRoom', 'switch', function (_data) {
                $that.adminMeterDetailRoomInfo.roomId = _data.roomId;
                $that._loadAdminMeterDetailRoomData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminMeterDetailRoom', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminMeterDetailRoomData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminMeterDetailRoomData: function (_page, _row) {
                let param = {
                    params: {
                        roomId:$that.adminMeterDetailRoomInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/room.queryAdminRooms',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.adminMeterDetailRoomInfo.rooms = _json.data;
                        vc.emit('adminMeterDetailRoom', 'paginationPlus', 'init', {
                            total: _json.records,
                            dataCount: _json.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);