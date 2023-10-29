/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingCouponInfo: {
                parkingCoupons: [],
                total: 0,
                records: 1,
                moreCondition: false,
                couponId: '',
                conditions: {
                    couponId: '',
                    name: '',
                    typeCd: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function () {
            $that._listParkingCoupons(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('parkingCoupon', 'listParkingCoupon', function (_param) {
                $that._listParkingCoupons(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listParkingCoupons(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingCoupons: function (_page, _rows) {
                $that.parkingCouponInfo.conditions.page = _page;
                $that.parkingCouponInfo.conditions.row = _rows;
                var param = {
                    params: $that.parkingCouponInfo.conditions
                };
                param.params.couponId = param.params.couponId.trim();
                param.params.name = param.params.name.trim();
                //发送get请求
                vc.http.apiGet('/parkingCoupon.listParkingCoupon',
                    param,
                    function (json, res) {
                        var _parkingCouponInfo = JSON.parse(json);
                        $that.parkingCouponInfo.total = _parkingCouponInfo.total;
                        $that.parkingCouponInfo.records = _parkingCouponInfo.records;
                        $that.parkingCouponInfo.parkingCoupons = _parkingCouponInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingCouponInfo.records,
                            dataCount: $that.parkingCouponInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddParkingCouponModal: function () {
                vc.emit('addParkingCoupon', 'openAddParkingCouponModal', {});
            },
            _openEditParkingCouponModel: function (_parkingCoupon) {
                vc.emit('editParkingCoupon', 'openEditParkingCouponModal', _parkingCoupon);
            },
            _openDeleteParkingCouponModel: function (_parkingCoupon) {
                vc.emit('deleteParkingCoupon', 'openDeleteParkingCouponModal', _parkingCoupon);
            },
            //查询
            _queryParkingCouponMethod: function () {
                $that._listParkingCoupons(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetParkingCouponMethod: function () {
                $that.parkingCouponInfo.conditions.couponId = "";
                $that.parkingCouponInfo.conditions.name = "";
                $that.parkingCouponInfo.conditions.typeCd = "";
                $that._listParkingCoupons(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.parkingCouponInfo.moreCondition) {
                    $that.parkingCouponInfo.moreCondition = false;
                } else {
                    $that.parkingCouponInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);