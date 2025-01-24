/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargeRulePriceInfo: {
                rules: [],
                ruleId:'',
                chargeType:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargeRulePrice', 'switch', function (_data) {
                $that.machineDetailChargeRulePriceInfo.ruleId = _data.ruleId;
                $that.machineDetailChargeRulePriceInfo.chargeType = _data.chargeType;
                $that._loadMachineDetailChargeRulePriceData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeRulePrice', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargeRulePriceData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailChargeRulePriceData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ruleId:$that.machineDetailChargeRulePriceInfo.ruleId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeRule.listChargeRulePrice',
                    param,
                    function (json) {
                        let _ruleInfo = JSON.parse(json);
                        $that.machineDetailChargeRulePriceInfo.rules = _ruleInfo.data;
                        vc.emit('machineDetailChargeRulePrice', 'paginationPlus', 'init', {
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