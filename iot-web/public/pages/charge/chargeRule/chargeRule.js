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
                //$that._switchChargeType($that.chargeRuleInfo.chargeTypes[0])
            })
        },
        _initEvent: function () {
            vc.on('chargeRule', 'switchChargeRule', function (_param) {
                $that.chargeRuleInfo.curChargeRule = _param;
                $that._changeChargeRuleTab(_param);
            })
        },
        methods: {
            _changeChargeRuleTab: function (_param) {
                //$that.chargeRuleInfo.tabName = _tabName;
                if (_param.chargeType == "1001") {
                    vc.emit('chargeRuleFee', 'switch', _param);
                } else if (_param.chargeType == "1002") {
                    vc.emit('chargeRulePrice', 'switch', _param);
                }
            },
            _switchChargeType: function (_chargeType) {
                $that.chargeRuleInfo.curChargeType = _chargeType;
                vc.emit('chargeType', 'switchChargeType', _chargeType);
            }
        },
    });
})(window.vc);