/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventTemplateParamInfo: {
                eventTemplateParams: [],
                total: 0,
                records: 1,
                conditions: {
                    templateId: '',
                    communityId: vc.getCurrentCommunity().communityId,
                }
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('eventTemplateParam', 'switch', function (_data) {
                $that.eventTemplateParamInfo.conditions.templateId = _data.templateId;
                $that._listEventTemplateParams(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventTemplateParamManage', 'listEventTemplateParam', function (_data) {
                $that._listEventTemplateParams(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventTemplateParam', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._listEventTemplateParams(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listEventTemplateParams: function (_page, _rows) {

                $that.eventTemplateParamInfo.conditions.page = _page;
                $that.eventTemplateParamInfo.conditions.row = _rows;
                var param = {
                    params: $that.eventTemplateParamInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/eventTemplateParam.listEventTemplateParam',
                    param,
                    function (json, res) {
                        var _eventTemplateParamInfo = JSON.parse(json);
                        $that.eventTemplateParamInfo.total = _eventTemplateParamInfo.total;
                        $that.eventTemplateParamInfo.records = _eventTemplateParamInfo.records;
                        $that.eventTemplateParamInfo.eventTemplateParams = _eventTemplateParamInfo.data;
                        vc.emit('eventTemplateParam', 'paginationPlus', 'init', {
                            total: $that.eventTemplateParamInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventTemplateParamModal: function () {
                vc.emit('addEventTemplateParam', 'openAddEventTemplateParamModal', {
                    templateId: $that.eventTemplateParamInfo.conditions.templateId,
                });
            },
            _openEditEventTemplateParamModel: function (_eventTemplateParam) {
                vc.emit('editEventTemplateParam', 'openEditEventTemplateParamModal', _eventTemplateParam);
            },
            _openDeleteEventTemplateParamModel: function (_eventTemplateParam) {
                vc.emit('deleteEventTemplateParam', 'openDeleteEventTemplateParamModal', _eventTemplateParam);
            },
            _queryEventTemplateParamMethod: function () {
                $that._listEventTemplateParams(DEFAULT_PAGE, DEFAULT_ROWS);
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