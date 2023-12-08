/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargeOrderInfo: {
                orders: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargeOrder', 'switch', function (_data) {
                $that.machineDetailChargeOrderInfo.machineId = _data.machineId;
                $that._loadMachineDetailChargeOrderData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeOrder', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargeOrderData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailChargeOrderData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailChargeOrderInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineOrder',
                    param,
                    function (json) {
                        let _orderInfo = JSON.parse(json);
                        $that.machineDetailChargeOrderInfo.orders = _orderInfo.data;
                        vc.emit('machineDetailChargeOrder', 'paginationPlus', 'init', {
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