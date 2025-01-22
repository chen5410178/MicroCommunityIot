/**
 入驻小区
 **/
(function (vc) {
    let DEFAULT_PAGE = 1;
    let DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            communityShopInfo: {
                shops: [],
                total: 0,
                records: 1,
                moreCondition: false,
                couponId: '',
                conditions: {
                    returnLink: '',
                    shopName: '',
                    shopId: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function () {
            $that._listCommunityShops(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('communityShop', 'listCommunityShop', function (_param) {
                $that._listCommunityShops(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listCommunityShops(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCommunityShops: function (_page, _rows) {
                $that.communityShopInfo.conditions.page = _page;
                $that.communityShopInfo.conditions.row = _rows;
                var param = {
                    params: $that.communityShopInfo.conditions
                };
                param.params.shopId = param.params.shopId.trim();
                param.params.shopName = param.params.shopName.trim();
                param.params.returnLink = param.params.returnLink.trim();
                //发送get请求
                vc.http.apiGet('/store.listCommunityStoreShop',
                    param,
                    function (json, res) {
                        var _communityShopInfo = JSON.parse(json);
                        $that.communityShopInfo.total = _communityShopInfo.total;
                        $that.communityShopInfo.records = _communityShopInfo.records;
                        $that.communityShopInfo.shops = _communityShopInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.communityShopInfo.records,
                            dataCount: $that.communityShopInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddCommunityShopModal: function () {
                vc.emit('addCommunityShop', 'openAddCommunityShopModal', {});
            },
            _openBuyParkingCouponModel: function (_shop) {
                vc.emit('buyParkingCoupon', 'openBuyParkingCouponModal', _shop);
            },
            _openDeleteCommunityShopModel: function (_parkingCoupon) {
                vc.emit('deleteCommunityShop', 'openDeleteCommunityShopModal', _parkingCoupon);
            },
            //查询
            _queryCommunityShopMethod: function () {
                $that._listCommunityShops(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetCommunityShopMethod: function () {
                $that.communityShopInfo.conditions.shopId = "";
                $that.communityShopInfo.conditions.shopName = "";
                $that.communityShopInfo.conditions.returnLink = "";
                $that._listCommunityShops(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            /*_resetStaffPwd: function (_shop) {
                vc.emit('resetStaffPwd', 'openResetStaffPwd', {
                    staffId: _shop.userId
                });
            },*/
            _viewShopCoupon: function (_shop) {
                vc.emit('viewShopCoupons', 'openViewShopCouponsModel', _shop);
            },
            _moreCondition: function () {
                if ($that.communityShopInfo.moreCondition) {
                    $that.communityShopInfo.moreCondition = false;
                } else {
                    $that.communityShopInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);