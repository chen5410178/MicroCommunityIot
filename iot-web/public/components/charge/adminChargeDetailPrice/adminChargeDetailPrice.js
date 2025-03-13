/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailPriceInfo: {
                rules: [],
                ruleId:'',
                chargeType:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailPrice', 'switch', function (_data) {
                $that.adminChargeDetailPriceInfo.ruleId = _data.ruleId;
                $that.adminChargeDetailPriceInfo.chargeType = _data.chargeType;
                $that._loadAdminChargeDetailPriceData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailPrice', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailPriceData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminChargeDetailPriceData: function (_page, _row) {
                let param = {
                    params: {
                        ruleId:$that.adminChargeDetailPriceInfo.ruleId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeRule.listAdminChargeRulePrice',
                    param,
                    function (json) {
                        let _ruleInfo = JSON.parse(json);
                        $that.adminChargeDetailPriceInfo.rules = _ruleInfo.data;
                        vc.emit('adminChargeDetailPrice', 'paginationPlus', 'init', {
                            total: _ruleInfo.records,
                            dataCount: _ruleInfo.total,
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