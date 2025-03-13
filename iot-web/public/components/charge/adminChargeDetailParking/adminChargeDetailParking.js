/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailParkingInfo: {
                coupons: [],
                machineId:'',
                orderId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailParking', 'switch', function (_data) {
                $that.adminChargeDetailParkingInfo.machineId = _data.machineId;
                $that.adminChargeDetailParkingInfo.orderId = _data.orderId;
                $that._loadAdminChargeDetailParkingData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailParking', 'list', function (_currentPage) {
                $that._loadAdminChargeDetailParkingData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailParking', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailParkingData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {

            _loadAdminChargeDetailParkingData: function (_page, _row) {
                let param = {
                    params: {
                        machineId:$that.adminChargeDetailParkingInfo.machineId,
                        orderId:$that.adminChargeDetailParkingInfo.orderId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/parkingCoupon.listAdminParkingCouponCharge',
                    param,
                    function (json) {
                        let _orderInfo = JSON.parse(json);
                        $that.adminChargeDetailParkingInfo.coupons = _orderInfo.data;
                        vc.emit('adminChargeDetailParking', 'paginationPlus', 'init', {
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