/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargeOrderAcctsInfo: {
                orderAccts: [],
                orderId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargeOrderAccts', 'switch', function (_data) {
                $that.machineDetailChargeOrderAcctsInfo.orderId = _data.orderId;
                $that._loadMachineDetailChargeOrderAcctsData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeOrderAccts', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargeOrderAcctsData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailChargeOrderAcctsData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        orderId:$that.machineDetailChargeOrderAcctsInfo.orderId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineOrderAcct',
                    param,
                    function (json) {
                        let _orderAcctsInfo = JSON.parse(json);
                        $that.machineDetailChargeOrderAcctsInfo.orderAccts = _orderAcctsInfo.data;
                        vc.emit('machineDetailChargeOrderAccts', 'paginationPlus', 'init', {
                            total: _orderAcctsInfo.records,
                            dataCount: _orderAcctsInfo.total,
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