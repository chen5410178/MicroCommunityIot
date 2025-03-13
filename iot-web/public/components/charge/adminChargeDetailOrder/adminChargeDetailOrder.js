/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailOrderInfo: {
                orders: [],
                machineId:'',
                orderId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailOrder', 'switch', function (_data) {
                $that.adminChargeDetailOrderInfo.machineId = _data.machineId;
                $that.adminChargeDetailOrderInfo.orderId = _data.orderId;
                $that._loadAdminChargeDetailOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailOrder', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailOrderData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminChargeDetailOrderData: function (_page, _row) {
                let param = {
                    params: {
                        machineId:$that.adminChargeDetailOrderInfo.machineId,
                        orderId:$that.adminChargeDetailOrderInfo.orderId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listAdminChargeMachineOrder',
                    param,
                    function (json) {
                        let _orderInfo = JSON.parse(json);
                        $that.adminChargeDetailOrderInfo.orders = _orderInfo.data;
                        vc.emit('adminChargeDetailOrder', 'paginationPlus', 'init', {
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