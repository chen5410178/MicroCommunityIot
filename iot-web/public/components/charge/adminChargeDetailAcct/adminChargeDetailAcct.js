/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailAcctInfo: {
                accounts: [],
                acctId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailAcct', 'switch', function (_data) {
                $that.adminChargeDetailAcctInfo.acctId = _data.acctId;
                $that._loadAdminChargeDetailAcctData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailAcct', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailAcctData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminChargeDetailAcctData: function (_page, _row) {
                let param = {
                    params: {
                        acctId:$that.adminChargeDetailAcctInfo.acctId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/account.queryAdminOwnerAccount',
                    param,
                    function (json) {
                        let _acctInfo = JSON.parse(json);
                        $that.adminChargeDetailAcctInfo.accounts = _acctInfo.data;
                        vc.emit('adminChargeDetailAcct', 'paginationPlus', 'init', {
                            total: _acctInfo.records,
                            dataCount: _acctInfo.total,
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