(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventRuleInfo: {
                curEventRule: {},
                tabName: 'eventObject'
            },
        },
        _initMethod: function() {},
        _initEvent: function() {
            vc.on('eventRule', 'switchEventRule', function(_param) {
                $that.eventRuleInfo.curEventRule = _param;
                $that._changeEventRuleTab('eventObject');
            })
        },
        methods: {
            _changeEventRuleTab: function(_tabName) {
                $that.eventRuleInfo.tabName = _tabName;
                if (_tabName === 'eventObject') {
                    vc.emit('eventObject', 'switch', { ruleId: $that.eventRuleInfo.curEventRule.ruleId });
                }
                if (_tabName === 'eventStaff') {
                    vc.emit('eventStaff', 'switch', { ruleId: $that.eventRuleInfo.curEventRule.ruleId });
                }
            }
        },
    });
})(window.vc);