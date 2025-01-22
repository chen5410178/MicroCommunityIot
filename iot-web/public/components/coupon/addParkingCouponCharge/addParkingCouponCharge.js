(function (vc) {

    vc.extends({
        data: {
            addParkingCouponChargeInfo: {
                pccId: '',
                couponShopId: '',
                shopId: '',
                communityId: '',
                machineId: '',
                shops:[],
                coupons:[]
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addParkingCouponCharge', 'openAddParkingCouponChargeModal', function (_param) {
                $('#addParkingCouponChargeModel').modal('show');
                $that.clearAddParkingCouponChargeInfo();
                $that.addParkingCouponChargeInfo.machineId = _param.machineId;
                $that._listAddCouponCommunityShops();
            });
        },
        methods: {
            addParkingCouponChargeValidate() {
                return vc.validate.validate({
                    addParkingCouponChargeInfo: $that.addParkingCouponChargeInfo
                }, {
                    'addParkingCouponChargeInfo.couponShopId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "商铺优惠券ID不能为空"
                        }
                    ],
                    'addParkingCouponChargeInfo.shopId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "商铺ID不能为空"
                        }
                    ],

                    'addParkingCouponChargeInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        } ],
                });
            },
            _listAddCouponCommunityShops: function () {
                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/store.listCommunityStoreShop',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addParkingCouponChargeInfo.shops = _json.data;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _changeAddShop:function(){
                let _shopId = $that.addParkingCouponChargeInfo.shopId;
                if(!_shopId){
                    return;
                }
                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId,
                        shopId:_shopId
                    }
                };
                //发送get请求
                vc.http.apiGet('/parkingCoupon.listShopParkingCoupon',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addParkingCouponChargeInfo.coupons = _json.data;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            saveParkingCouponChargeInfo: function () {
                if (!$that.addParkingCouponChargeValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                $that.addParkingCouponChargeInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/parkingCoupon.saveParkingCouponCharge',
                    JSON.stringify($that.addParkingCouponChargeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addParkingCouponChargeModel').modal('hide');
                            $that.clearAddParkingCouponChargeInfo();
                            vc.emit('machineDetailChargeParking', 'list', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearAddParkingCouponChargeInfo: function () {
                $that.addParkingCouponChargeInfo = {
                    couponShopId: '',
                    shopId: '',
                    communityId: '',
                    machineId: '',
                    shops:[],
                    coupons:[]
                };
            }
        }
    });

})(window.vc);
