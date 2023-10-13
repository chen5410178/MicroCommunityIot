/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventTemplateManageInfo: {
                eventTemplates: [],
                total: 0,
                records: 1,
                moreCondition: false,
                templateId: '',
                conditions: {
                    templateName: '',
                    eventWay: '',
                    communityId: vc.getCurrentCommunity().communityId,
                }
            },
            eventWayList: [],
        },
        _initMethod: function () {
            vc.getDict('event_template', 'event_way', function (_data) {
                $that.eventWayList = _data;
            })
            $that._listEventTemplates(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('eventTemplateManage', 'listEventTemplate', function (_param) {
                $that._listEventTemplates(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listEventTemplates(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listEventTemplates: function (_page, _rows) {

                $that.eventTemplateManageInfo.conditions.page = _page;
                $that.eventTemplateManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.eventTemplateManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/eventTemplate.listEventTemplate',
                    param,
                    function (json, res) {
                        var _eventTemplateManageInfo = JSON.parse(json);
                        $that.eventTemplateManageInfo.total = _eventTemplateManageInfo.total;
                        $that.eventTemplateManageInfo.records = _eventTemplateManageInfo.records;
                        $that.eventTemplateManageInfo.eventTemplates = _eventTemplateManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.eventTemplateManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventTemplateModal: function () {
                vc.emit('addEventTemplate', 'openAddEventTemplateModal', {});
            },
            _openEditEventTemplateModel: function (_eventTemplate) {
                vc.emit('editEventTemplate', 'openEditEventTemplateModal', _eventTemplate);
            },
            _openDeleteEventTemplateModel: function (_eventTemplate) {
                vc.emit('deleteEventTemplate', 'openDeleteEventTemplateModal', _eventTemplate);
            },
            _queryEventTemplateMethod: function () {
                $that._listEventTemplates(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetEventTemplateMethod: function () {
                $that.eventTemplateManageInfo.conditions = {
                    templateName: '',
                    eventWay: '',
                    communityId: vc.getCurrentCommunity().communityId,
                };
                $that._listEventTemplates(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openModel: function (_data, _title) {
                vc.emit('viewData', 'openEventViewDataModal', {
                    title: _title,
                    data: _data
                });
            },
        }
    });
})(window.vc);
