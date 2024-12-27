/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingCouponCarInfo: {
                parkingCouponCars: [],
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
            $that._listParkingCouponCars(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.parkingCouponCarInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.parkingCouponCarInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('parkingCouponCar', 'listParkingCouponCar', function (_param) {
                $that._listParkingCouponCars(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listParkingCouponCars(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingCouponCars: function (_page, _rows) {
                $that.parkingCouponCarInfo.conditions.page = _page;
                $that.parkingCouponCarInfo.conditions.row = _rows;
                $that.parkingCouponCarInfo.conditions.communityId =vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.parkingCouponCarInfo.conditions
                };
                param.params.carNumLike = param.params.carNumLike.trim();
                param.params.shopNameLike = param.params.shopNameLike.trim();
                //发送get请求
                vc.http.apiGet('/parkingCoupon.listParkingCouponCar',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingCouponCarInfo.total = _json.total;
                        $that.parkingCouponCarInfo.records = _json.records;
                        $that.parkingCouponCarInfo.parkingCouponCars = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingCouponCarInfo.records,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _queryParkingCouponCarMethod: function () {
                $that._listParkingCouponCars(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetParkingCouponCarMethod: function () {
                $that.parkingCouponCarInfo.conditions.carNumLike = "";
                $that.parkingCouponCarInfo.conditions.shopNameLike = "";
                $that.parkingCouponCarInfo.conditions.state = "";
                $that.parkingCouponCarInfo.conditions.queryStartTime = "";
                $that.parkingCouponCarInfo.conditions.queryEndTime = "";
                $that._listParkingCouponCars(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.parkingCouponCarInfo.moreCondition) {
                    $that.parkingCouponCarInfo.moreCondition = false;
                } else {
                    $that.parkingCouponCarInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);