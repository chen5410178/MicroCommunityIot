/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingCouponOrderInfo: {
                parkingCoupons: [],
                total: 0,
                records: 1,
                moreCondition: false,
                couponId: '',
                conditions: {
                    couponId: '',
                    shopName: '',
                    typeCd: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function () {
            $that._listParkingCouponOrders(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('parkingCouponOrder', 'listParkingCouponOrder', function (_param) {
                $that._listParkingCouponOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listParkingCouponOrders(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingCouponOrders: function (_page, _rows) {
                $that.parkingCouponOrderInfo.conditions.page = _page;
                $that.parkingCouponOrderInfo.conditions.row = _rows;
                var param = {
                    params: $that.parkingCouponOrderInfo.conditions
                };
                param.params.couponId = param.params.couponId.trim();
                param.params.shopName = param.params.shopName.trim();
                //发送get请求
                vc.http.apiGet('/parkingCoupon.listParkingCouponOrder',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingCouponOrderInfo.total = _json.total;
                        $that.parkingCouponOrderInfo.records = _json.records;
                        $that.parkingCouponOrderInfo.parkingCoupons = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingCouponOrderInfo.records,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _queryParkingCouponOrderMethod: function () {
                $that._listParkingCouponOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetParkingCouponOrderMethod: function () {
                $that.parkingCouponOrderInfo.conditions.couponId = "";
                $that.parkingCouponOrderInfo.conditions.shopName = "";
                $that._listParkingCouponOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.parkingCouponOrderInfo.moreCondition) {
                    $that.parkingCouponOrderInfo.moreCondition = false;
                } else {
                    $that.parkingCouponOrderInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);