/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargeParkingInfo: {
                coupons: [],
                machineId:'',
                orderId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargeParking', 'switch', function (_data) {
                $that.machineDetailChargeParkingInfo.machineId = _data.machineId;
                $that.machineDetailChargeParkingInfo.orderId = _data.orderId;
                $that._loadMachineDetailChargeParkingData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeParking', 'list', function (_currentPage) {
                $that._loadMachineDetailChargeParkingData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeParking', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargeParkingData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _openAddParkingCouponChargeModal:function(){
                vc.emit('addParkingCouponCharge','openAddParkingCouponChargeModal',{
                    machineId:$that.machineDetailChargeParkingInfo.machineId
                });
            },
            _openDeleteParkingCouponChargeModel:function(_coupon){
                vc.emit('deleteParkingCouponCharge','openDeleteParkingCouponChargeModal',_coupon);
            },
            _loadMachineDetailChargeParkingData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailChargeParkingInfo.machineId,
                        orderId:$that.machineDetailChargeParkingInfo.orderId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/parkingCoupon.listParkingCouponCharge',
                    param,
                    function (json) {
                        let _orderInfo = JSON.parse(json);
                        $that.machineDetailChargeParkingInfo.coupons = _orderInfo.data;
                        vc.emit('machineDetailChargeParking', 'paginationPlus', 'init', {
                            total: _orderInfo.records,
                            dataCount: _orderInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);