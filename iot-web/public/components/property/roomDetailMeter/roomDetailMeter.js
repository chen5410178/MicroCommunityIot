/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailMeterInfo: {
                meterMachines: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailMeter', 'switch', function (_data) {
                $that.roomDetailMeterInfo.roomId = _data.roomId;
                $that._loadRoomDetailMeterData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailMeter', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailMeterData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailMeter', 'notify', function (_data) {
                $that._loadRoomDetailMeterData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailMeterData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailMeterInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachine',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailMeterInfo.meterMachines = _roomInfo.data;
                        vc.emit('roomDetailMeter', 'paginationPlus', 'init', {
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
            _qureyRoomDetailMeter: function () {
                $that._loadRoomDetailMeterData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);