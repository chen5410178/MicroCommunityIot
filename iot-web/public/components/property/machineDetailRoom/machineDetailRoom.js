/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailRoomInfo: {
                rooms: [],
                roomId:'',
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailRoom', 'switch', function (_data) {
                $that.machineDetailRoomInfo.roomId = _data.roomId;
                $that._loadMachineDetailRoomData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailRoom', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailRoomData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailRoomData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.machineDetailRoomInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/room.queryRooms',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.machineDetailRoomInfo.rooms = _roomInfo.data;
                        vc.emit('machineDetailRoom', 'paginationPlus', 'init', {
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
        }
    });
})(window.vc);