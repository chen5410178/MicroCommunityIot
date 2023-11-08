/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailMeterRechargeInfo: {
                details: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailMeterRecharge', 'switch', function (_data) {
                $that.roomDetailMeterRechargeInfo.roomId = _data.roomId;
                $that._loadRoomDetailMeterRechargeData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailMeterRecharge', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailMeterRechargeData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailMeterRecharge', 'notify', function (_data) {
                $that._loadRoomDetailMeterRechargeData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailMeterRechargeData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailMeterRechargeInfo.roomId,
                        page:_page,
                        row:_row,
                        detailType: '1001'
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachineReadOrRechargeDetail',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailMeterRechargeInfo.details = _roomInfo.data;
                        vc.emit('roomDetailMeterRecharge', 'paginationPlus', 'init', {
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
            _qureyRoomDetailMeterRecharge: function () {
                $that._loadRoomDetailMeterRechargeData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);