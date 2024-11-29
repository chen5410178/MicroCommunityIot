/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeUserCouponInfo: {
                couponPropertyUsers: [],
                total: 0,
                records: 1,
                moreCondition: false,
                couponId: '',
                conditions: {
                    couponId: '',
                    couponName: '',
                    validityDay: '',
                    userName: '',
                    tel: '',
                    toType: '5005',
                    state: '',
                    communityId:''
                }
            }
        },
        _initMethod: function () {
            $that._listCouponPropertyUsers(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('chargeUserCoupon', 'listCouponPropertyUser', function (_param) {
                $that._listCouponPropertyUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listCouponPropertyUsers(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCouponPropertyUsers: function (_page, _rows) {

                $that.chargeUserCouponInfo.conditions.page = _page;
                $that.chargeUserCouponInfo.conditions.row = _rows;
                $that.chargeUserCouponInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.chargeUserCouponInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/coupon.listCouponPropertyUser',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeUserCouponInfo.total = _json.total;
                        $that.chargeUserCouponInfo.records = _json.records;
                        $that.chargeUserCouponInfo.couponPropertyUsers = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeUserCouponInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryCouponPropertyUserMethod: function () {
                $that._listCouponPropertyUsers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.chargeUserCouponInfo.moreCondition) {
                    $that.chargeUserCouponInfo.moreCondition = false;
                } else {
                    $that.chargeUserCouponInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
