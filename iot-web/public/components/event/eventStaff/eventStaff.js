/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventStaffInfo: {
                eventStaffs: [],
                total: 0,
                records: 1,
                conditions: {
                    ruleId: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    staffName: '',
                    state: ''
                }
            },
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('eventStaff', 'switch', function (_data) {
                $that.eventStaffInfo.conditions.ruleId = _data.ruleId;
                $that._listEventStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventStaffManage', 'listEventStaff', function (_param) {
                $that._listEventStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('eventStaff', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._listEventStaffs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listEventStaffs: function (_page, _rows) {

                $that.eventStaffInfo.conditions.page = _page;
                $that.eventStaffInfo.conditions.row = _rows;
                var param = {
                    params: $that.eventStaffInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/eventStaff.listEventStaff',
                    param,
                    function (json, res) {
                        var _eventStaffInfo = JSON.parse(json);
                        $that.eventStaffInfo.total = _eventStaffInfo.total;
                        $that.eventStaffInfo.records = _eventStaffInfo.records;
                        $that.eventStaffInfo.eventStaffs = _eventStaffInfo.data;
                        vc.emit('eventStaff', 'paginationPlus', 'init', {
                            total: $that.eventStaffInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddEventStaffModal: function () {
                vc.jumpToPage('/#/pages/event/addEventStaff?ruleId=' + $that.eventStaffInfo.conditions.ruleId);
            },
            _openDeleteEventStaffModel: function (_eventStaff) {
                vc.emit('deleteEventStaff', 'openDeleteEventStaffModal', _eventStaff);
            },
            _queryEventStaffMethod: function () {
                $that._listEventStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetEventStaffMethod: function () {
                $that.eventStaffInfo.conditions.staffName = '';
                $that.eventStaffInfo.conditions.state = '';
                $that._listEventStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _changeEventStaffState: function (_eventStaff) {
                if (_eventStaff.state === 'ON') {
                    _eventStaff.state = 'OFF';
                } else if (_eventStaff.state === 'OFF') {
                    _eventStaff.state = 'ON';
                }
                vc.http.apiPost(
                    '/eventStaff.updateEventStaff',
                    JSON.stringify(_eventStaff),
                    {
                        emulateJSON:true
                    },
                    function(json,res){
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            vc.emit('eventStaffManage','listEventStaff',{});
                            return ;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo,error){
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
        }
    });
})(window.vc);