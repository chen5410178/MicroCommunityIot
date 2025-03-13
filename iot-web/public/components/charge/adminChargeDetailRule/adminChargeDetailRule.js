/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailRuleInfo: {
                rules: [],
                ruleId:'',
                chargeType:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailRule', 'switch', function (_data) {
                $that.adminChargeDetailRuleInfo.ruleId = _data.ruleId;
                $that.adminChargeDetailRuleInfo.chargeType = _data.chargeType;
                $that._loadAdminChargeDetailRuleData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailRule', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailRuleData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminChargeDetailRuleData: function (_page, _row) {
                let param = {
                    params: {
                        ruleId:$that.adminChargeDetailRuleInfo.ruleId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeRule.listAdminChargeRuleFee',
                    param,
                    function (json) {
                        let _ruleInfo = JSON.parse(json);
                        $that.adminChargeDetailRuleInfo.rules = _ruleInfo.data;
                        vc.emit('adminChargeDetailRule', 'paginationPlus', 'init', {
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