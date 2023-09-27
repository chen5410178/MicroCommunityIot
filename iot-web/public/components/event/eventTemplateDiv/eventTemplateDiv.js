/**
 入驻小区
 **/
(function (vc) {
    vc.extends({
        data: {
            eventTemplateDivInfo: {
                eventTemplateList: [],
                curEventTemplate: {}
            },
            eventWayList: [],
        },
        _initMethod: function () {
            vc.getDict('event_template', 'event_way', function (_data) {
                $that.eventWayList = _data
            })
            $that._listEventTemplates();
        },
        _initEvent: function () {
            vc.on('eventTemplateManage', 'listEventTemplate', function (_param) {
                $that._listEventTemplates();
            });
        },
        methods: {
            _listEventTemplates: function () {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/eventTemplate.listEventTemplate',
                    param,
                    function (json, res) {
                        let _eventTemplateManageInfo = JSON.parse(json);
                        $that.eventTemplateDivInfo.total = _eventTemplateManageInfo.total;
                        $that.eventTemplateDivInfo.records = _eventTemplateManageInfo.records;
                        $that.eventTemplateDivInfo.eventTemplateList = _eventTemplateManageInfo.data;
                        if ($that.eventTemplateDivInfo.eventTemplateList && $that.eventTemplateDivInfo.eventTemplateList.length > 0) {
                            $that._switchEventTemplate($that.eventTemplateDivInfo.eventTemplateList[0])
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventTemplateModal: function () {
                vc.emit('addEventTemplate', 'openAddEventTemplateModal', {});
            },
            _openEditEventTemplateModel: function () {
                if (!$that.eventTemplateDivInfo.curEventTemplate) {
                    vc.toast('未选择事件模板');
                    return;
                }
                vc.emit('editEventTemplate', 'openEditEventTemplateModal', $that.eventTemplateDivInfo.curEventTemplate);
            },
            _openDeleteEventTemplateModel: function () {
                if (!$that.eventTemplateDivInfo.curEventTemplate) {
                    vc.toast('未选择事件模板');
                    return;
                }
                vc.emit('deleteEventTemplate', 'openDeleteEventTemplateModal', $that.eventTemplateDivInfo.curEventTemplate);
            },
            _switchEventTemplate: function (_eventTemplate) {
                $that.eventTemplateDivInfo.curEventTemplate = _eventTemplate;
                vc.emit('eventTemplate', 'switchEventTemplate', _eventTemplate);
                vc.emit('eventTemplateParam', 'switch', _eventTemplate);
            },
        }
    });
})(window.vc);