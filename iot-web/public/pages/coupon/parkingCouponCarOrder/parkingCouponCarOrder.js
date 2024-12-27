/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingCouponCarOrderInfo: {
                orders: [],
                total: 0,
                records: 1,
                moreCondition: false,
                couponId: '',
                conditions: {
                    couponId: '',
                    shopNameLike: '',
                    carNumLike: '',
                    state: '',
                    communityId: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function () {
            $that._listParkingCouponCarOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.parkingCouponCarOrderInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.parkingCouponCarOrderInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('parkingCouponCarOrder', 'listParkingCouponCarOrder', function (_param) {
                $that._listParkingCouponCarOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listParkingCouponCarOrders(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingCouponCarOrders: function (_page, _rows) {
                $that.parkingCouponCarOrderInfo.conditions.page = _page;
                $that.parkingCouponCarOrderInfo.conditions.row = _rows;
                $that.parkingCouponCarOrderInfo.conditions.communityId =vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.parkingCouponCarOrderInfo.conditions
                };
                param.params.carNumLike = param.params.carNumLike.trim();
                param.params.shopNameLike = param.params.shopNameLike.trim();
                //发送get请求
                vc.http.apiGet('/parkingCouponCar.listParkingCouponCarOrder',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingCouponCarOrderInfo.total = _json.total;
                        $that.parkingCouponCarOrderInfo.records = _json.records;
                        $that.parkingCouponCarOrderInfo.orders = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingCouponCarOrderInfo.records,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _queryParkingCouponCarOrderMethod: function () {
                $that._listParkingCouponCarOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetParkingCouponCarOrderMethod: function () {
                $that.parkingCouponCarOrderInfo.conditions.carNumLike = "";
                $that.parkingCouponCarOrderInfo.conditions.shopNameLike = "";
                $that.parkingCouponCarOrderInfo.conditions.queryStartTime = "";
                $that.parkingCouponCarOrderInfo.conditions.queryEndTime = "";
                $that._listParkingCouponCarOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.parkingCouponCarOrderInfo.moreCondition) {
                    $that.parkingCouponCarOrderInfo.moreCondition = false;
                } else {
                    $that.parkingCouponCarOrderInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);