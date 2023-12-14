/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            carDetailMonthOrderInfo: {
                carDetailMonthOrders: [],
                carId: '',
                memberId:'',
                carNum:'',
                paId:''
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('carDetailMonthOrder', 'switch', function (_data) {
                $that.carDetailMonthOrderInfo.carId = _data.carId;
                $that.carDetailMonthOrderInfo.carNum = _data.carNum;
                $that.carDetailMonthOrderInfo.paId = _data.paId;
                $that.carDetailMonthOrderInfo.memberId = _data.memberId;
                $that._loadCarDetailMonthOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('carDetailMonthOrder', 'notify',
                function (_data) {
                    $that._loadCarDetailMonthOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
                });
            vc.on('carDetailMonthOrder', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadCarDetailMonthOrderData(_currentPage, DEFAULT_ROWS);
                });
        },
        methods: {
            _loadCarDetailMonthOrderData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        carNum: $that.carDetailMonthOrderInfo.carNum,
                        page: _page,
                        row: _row
                    }
                };

                //发送get请求
                vc.http.apiGet('/carMonth.listCarMonthOrder',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.carDetailMonthOrderInfo.carDetailMonthOrders = _roomInfo.data;
                        vc.emit('carDetailMonthOrder', 'paginationPlus', 'init', {
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
            _qureyCarDetailMonthOrder: function () {
                $that._loadCarDetailMonthOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);