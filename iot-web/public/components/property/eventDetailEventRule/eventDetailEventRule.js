/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventDetailEventRuleInfo: {
                rules: [],
                ruleId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('eventDetailEventRule', 'switch', function (_data) {
                $that.eventDetailEventRuleInfo.ruleId = _data.ruleId;
                $that._loadMachineDetailEventRuleData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventDetailEventRule', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailEventRuleData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailEventRuleData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        ruleId:$that.eventDetailEventRuleInfo.ruleId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/eventRule.listEventRule',
                    param,
                    function (json) {
                        let _eventInfo = JSON.parse(json);
                        $that.eventDetailEventRuleInfo.rules = _eventInfo.data;
                        vc.emit('eventDetailEventRule', 'paginationPlus', 'init', {
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
        }
    });
})(window.vc);