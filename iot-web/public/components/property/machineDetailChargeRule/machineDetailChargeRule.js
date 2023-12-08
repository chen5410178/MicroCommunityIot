/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargeRuleInfo: {
                rules: [],
                ruleId:'',
                chargeType:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargeRule', 'switch', function (_data) {
                $that.machineDetailChargeRuleInfo.ruleId = _data.ruleId;
                $that.machineDetailChargeRuleInfo.chargeType = _data.chargeType;
                $that._loadMachineDetailChargeRuleData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargeRule', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargeRuleData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailChargeRuleData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ruleId:$that.machineDetailChargeRuleInfo.ruleId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeRule.listChargeRuleFee',
                    param,
                    function (json) {
                        let _ruleInfo = JSON.parse(json);
                        $that.machineDetailChargeRuleInfo.rules = _ruleInfo.data;
                        vc.emit('machineDetailChargeRule', 'paginationPlus', 'init', {
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