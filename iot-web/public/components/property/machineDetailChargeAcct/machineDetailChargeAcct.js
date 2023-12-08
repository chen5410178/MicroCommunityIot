/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargeAcctInfo: {
                accounts: [],
                acctId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargeAcct', 'switch', function (_data) {
                $that.machineDetailChargeAcctInfo.acctId = _data.acctId;
                $that._loadMachineDetailChargeAcctData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeAcct', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargeAcctData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailChargeAcctData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        acctId:$that.machineDetailChargeAcctInfo.acctId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/account.queryOwnerAccount',
                    param,
                    function (json) {
                        let _acctInfo = JSON.parse(json);
                        $that.machineDetailChargeAcctInfo.accounts = _acctInfo.data;
                        vc.emit('machineDetailChargeAcct', 'paginationPlus', 'init', {
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