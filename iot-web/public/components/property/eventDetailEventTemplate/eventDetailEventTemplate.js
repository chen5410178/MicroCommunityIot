/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventDetailEventTemplateInfo: {
                templates: [],
                ruleId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('eventDetailEventTemplate', 'switch', function (_data) {
                $that.eventDetailEventTemplateInfo.ruleId = _data.ruleId;
                $that._loadMachineDetailEventTemplateData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventDetailEventTemplate', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailEventTemplateData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailEventTemplateData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ruleId:$that.eventDetailEventTemplateInfo.ruleId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/eventRule.listEventRule',
                    param,
                    function (json) {
                        let _eventInfo = JSON.parse(json);
                        $that.eventDetailEventTemplateInfo.templates = [];
                        $that.eventDetailEventTemplateInfo.templates.push(_eventInfo.data[0].eventTemplate);
                        vc.emit('eventDetailEventTemplate', 'paginationPlus', 'init', {
                            total: _eventInfo.records,
                            dataCount: _eventInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            _openViewEventTemplateParamModel: function (_template) {
                vc.emit('viewEventTemplateParam', 'openViewEventTemplateParamModal', _template.eventTemplateParamDtoList);
            }
        }
    });
})(window.vc);