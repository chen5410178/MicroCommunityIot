(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeRuleInfo: {
                curChargeRule: {},
                tabName: 'chargeRuleConfig',
                chargeTypes: [],
                curChargeType: {}
            },
        },
        _initMethod: function () {
            vc.getDict('', 'charge_type', function (_data) {
                $that.chargeRuleInfo.chargeTypes = _data;
                $that._switchChargeType($that.chargeRuleInfo.chargeTypes[0])
            })
        },
        _initEvent: function () {
            vc.on('chargeRule', 'switchChargeRule', function (_param) {
                $that.chargeRuleInfo.curChargeRule = _param;
                $that._changeChargeRuleTab('chargeRuleConfig');
            })
        },
        methods: {
            _changeChargeRuleTab: function (_tabName) {
                $that.chargeRuleInfo.tabName = _tabName;
                vc.emit('chargeRuleFee', 'switch', { ruleId: $that.chargeRuleInfo.curChargeRule.ruleId });
            },
            _switchChargeType: function (_chargeType) {
                $that.chargeRuleInfo.curChargeType = _chargeType;
                vc.emit('chargeType', 'switchChargeType', _chargeType);
            }
        },
    });
})(window.vc);