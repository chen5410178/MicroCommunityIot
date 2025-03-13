/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailOrderAcctsInfo: {
                orderAccts: [],
                orderId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailOrderAccts', 'switch', function (_data) {
                $that.adminChargeDetailOrderAcctsInfo.orderId = _data.orderId;
                $that._loadAdminChargeDetailOrderAcctsData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailOrderAccts', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailOrderAcctsData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminChargeDetailOrderAcctsData: function (_page, _row) {
                let param = {
                    params: {
                        orderId:$that.adminChargeDetailOrderAcctsInfo.orderId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listAdminChargeMachineOrderAcct',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.adminChargeDetailOrderAcctsInfo.orderAccts = _json.data;
                        vc.emit('adminChargeDetailOrderAccts', 'paginationPlus', 'init', {
                            total: _json.records,
                            dataCount: _json.total,
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