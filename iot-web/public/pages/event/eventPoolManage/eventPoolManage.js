/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventPoolManageInfo: {
                eventPools: [],
                total: 0,
                records: 1,
                moreCondition: false,
                eventId: '',
                conditions: {
                    ruleId: '',
                    eventType: '',
                    objType: '',
                    objectName: '',
                    fromType: '',
                    state: '',
                }
            },
            eventRuleList: [],
            eventTypeList: [],
            objTypeList: [],
        },
        _initMethod: function () {
            $that.listEventRules();
            vc.getDict('event_pool', 'event_type', function (_data) {
                $that.eventTypeList = _data;
            });
            vc.getDict('event_object', 'obj_type', function (_data) {
                $that.objTypeList = _data;
            });
            $that._listEventPools(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('eventPoolManage', 'listEventPool', function (_param) {
                $that._listEventPools(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listEventPools(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listEventPools: function (_page, _rows) {

                $that.eventPoolManageInfo.conditions.page = _page;
                $that.eventPoolManageInfo.conditions.row = _rows;
                $that.eventPoolManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.eventPoolManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/eventPool.listEventPool',
                    param,
                    function (json, res) {
                        var _eventPoolManageInfo = JSON.parse(json);
                        $that.eventPoolManageInfo.total = _eventPoolManageInfo.total;
                        $that.eventPoolManageInfo.records = _eventPoolManageInfo.records;
                        $that.eventPoolManageInfo.eventPools = _eventPoolManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.eventPoolManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventPoolModal: function () {
                vc.jumpToPage('/#/pages/event/addEventPoolManage');
            },
            _openDeleteEventPoolModel: function (_eventPool) {
                vc.emit('deleteEventPool', 'openDeleteEventPoolModal', _eventPool);
            },
            _queryEventPoolMethod: function () {
                $that._listEventPools(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetEventPoolMethod: function () {
                $that.eventPoolManageInfo.conditions = {
                    ruleId: '',
                    eventType: '',
                    objType: '',
                    objectName: '',
                    fromType: '',
                    state: '',
                };
                $that._listEventPools(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.eventPoolManageInfo.moreCondition) {
                    $that.eventPoolManageInfo.moreCondition = false;
                } else {
                    $that.eventPoolManageInfo.moreCondition = true;
                }
            },
            listEventRules: function () {
                let param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/eventRule.listEventRule',
                    param,
                    function (json, res) {
                        let _eventRuleManageInfo = JSON.parse(json);
                        $that.eventRuleList = _eventRuleManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
