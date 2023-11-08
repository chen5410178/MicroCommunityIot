/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailCarInfo: {
                cars: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailCar', 'switch', function (_data) {
                $that.roomDetailCarInfo.roomId = _data.roomId;
                $that._loadRoomDetailCarData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailCar', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailCarData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailCar', 'notify', function (_data) {
                $that._loadRoomDetailCarData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailCarData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailCarInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/ownerCar.queryRoomCars',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailCarInfo.cars = _roomInfo.data;
                        vc.emit('roomDetailCar', 'paginationPlus', 'init', {
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
            _qureyRoomDetailCar: function () {
                $that._loadRoomDetailCarData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);