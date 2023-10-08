/**
 入驻小区
 **/
(function (vc) {
    vc.extends({
        data: {
            eventRuleDivInfo: {
                eventRules: [],
                ruleId: '',
                curEventRule: {}
            },
            eventTemplateList: [],
        },
        _initMethod: function () {
            $that._listEventRules();
            $that._listEventTemplates();
        },
        _initEvent: function () {
            vc.on('eventRuleManage', 'listEventRule', function (_param) {
                $that._listEventRules();
            });
        },
        methods: {
            _listEventRules: function () {

                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/eventRule.listEventRule',
                    param,
                    function (json, res) {
                        let _eventRuleManageInfo = JSON.parse(json);
                        $that.eventRuleDivInfo.total = _eventRuleManageInfo.total;
                        $that.eventRuleDivInfo.records = _eventRuleManageInfo.records;
                        $that.eventRuleDivInfo.eventRules = _eventRuleManageInfo.data;
                        if($that.eventRuleDivInfo.eventRules && $that.eventRuleDivInfo.eventRules.length>0){
                            $that._switchEventRule($that.eventRuleDivInfo.eventRules[0])
                        }
                        
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventRuleModal: function () {
                vc.emit('addEventRule', 'openAddEventRuleModal', {});
            },
            _openEditEventRuleModel: function () {
                if (!$that.eventRuleDivInfo.curEventRule) {
                    vc.toast('未选择事件规则');
                    return;
                }
                vc.emit('editEventRule', 'openEditEventRuleModal', $that.eventRuleDivInfo.curEventRule);
            },
            _openDeleteEventRuleModel: function () {
                if (!$that.eventRuleDivInfo.curEventRule) {
                    vc.toast('未选择事件规则');
                    return;
                }
                vc.emit('deleteEventRule', 'openDeleteEventRuleModal', $that.eventRuleDivInfo.curEventRule);
            },
            _switchEventRule: function (_eventRule) {
                $that.eventRuleDivInfo.curEventRule = _eventRule;
                vc.emit('eventRule', 'switchEventRule', _eventRule);
            },
            _listEventTemplates: function () {
                let param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/eventTemplate.listEventTemplate',
                    param,
                    function (json, res) {
                        let _eventTemplateManageInfo = JSON.parse(json);
                        $that.eventTemplateList = _eventTemplateManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);