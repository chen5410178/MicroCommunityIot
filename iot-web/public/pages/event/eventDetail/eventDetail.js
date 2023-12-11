/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            eventDetailInfo: {
                eventId: "",
                eventMsg: "",
                eventTypeName: "",
                objTypeName: "",
                ruleId: "",
                ruleName: "",
                fromName: "",
                stateName: "",
                createTime: "",
                eventPoolStaffList: [],
                url: '',
                _currentTab: 'eventDetailEventRule',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.eventDetailInfo.eventId = vc.getParam('eventId');
            $that.eventDetailInfo.ruleId = vc.getParam('ruleId');
            if (!vc.notNull($that.eventDetailInfo.eventId)) {
                return;
            }
            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.eventDetailInfo._currentTab = _currentTab;
            }

            $that._loadEventInfo();
            $that.changeTab($that.eventDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.eventDetailInfo._currentTab);
            });
        },
        methods: {
            _loadEventInfo: function() {
                let param = {
                        params: {
                            eventId: $that.eventDetailInfo.eventId,
                            page: 1,
                            row: 10,
                            communityId: vc.getCurrentCommunity().communityId,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/eventPool.listEventPool',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        switch (_json.data[0].state) {
                            case "W":
                                _json.data[0].stateName = "待处理";
                                break;
                            case "NC":
                                _json.data[0].stateName = "正常处理完成";
                                break;
                            case "TC":
                                _json.data[0].stateName = "超时处理完成";
                                break;
                            case "D":
                                _json.data[0].stateName = "处理中";
                                break;
                            default:
                                break;
                        }
                        vc.copyObject(_json.data[0], $that.eventDetailInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.eventDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    ruleId: $that.eventDetailInfo.ruleId,
                })
            },
        }
    });
})(window.vc);