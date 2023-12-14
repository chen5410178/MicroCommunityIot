/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            carMonthOrderInfo: {
                carMonthOrders: [],
                total: 0,
                records: 1,
                moreCondition: false,
                orderId: '',
                conditions: {
                    orderId: '',
                    cardId: '',
                    carNum: '',
                    primeRate: '',
                    state: '',
                }
            }
        },
        _initMethod: function () {
            $that._listCarMonthOrders(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('carMonthOrder', 'listCarMonthOrder', function (_param) {
                $that._listCarMonthOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listCarMonthOrders(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCarMonthOrders: function (_page, _rows) {

                $that.carMonthOrderInfo.conditions.page = _page;
                $that.carMonthOrderInfo.conditions.row = _rows;
                $that.carMonthOrderInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.carMonthOrderInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/carMonth.listCarMonthOrder',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.carMonthOrderInfo.total = _json.total;
                        $that.carMonthOrderInfo.records = _json.records;
                        $that.carMonthOrderInfo.carMonthOrders = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.carMonthOrderInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openBuyMonthCardModal: function () {
                vc.emit('buyCarMonthCard', 'openBuyCarMonthCardModal', {});
            },
            _openRetundModel: function (_carMonthOrder) {
                vc.emit('retundCarMonthOrder','openRetundOrderModal', _carMonthOrder);
            },
            _queryCarMonthOrderMethod: function () {
                $that._listCarMonthOrders(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.carMonthOrderInfo.moreCondition) {
                    $that.carMonthOrderInfo.moreCondition = false;
                } else {
                    $that.carMonthOrderInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
