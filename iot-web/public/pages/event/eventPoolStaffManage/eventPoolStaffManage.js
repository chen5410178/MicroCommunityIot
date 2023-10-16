/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            eventPoolStaffManageInfo: {
                eventPoolStaffs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                epsId: '',
                conditions: {
                    eventId: '',
                    state: '',
                }
            },
            event: {}
        },
        _initMethod: function () {
            $that._listEventPoolStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('eventPoolStaffManage', 'listEventPoolStaff', function (_param) {
                $that._listEventPoolStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listEventPoolStaffs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listEventPoolStaffs: function (_page, _rows) {

                $that.eventPoolStaffManageInfo.conditions.page = _page;
                $that.eventPoolStaffManageInfo.conditions.row = _rows;
                $that.eventPoolStaffManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                $that.eventPoolStaffManageInfo.conditions.staffId = vc.getData('/nav/getUserInfo').userId;
                var param = {
                    params: $that.eventPoolStaffManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/eventPoolStaff.listEventPoolStaff',
                    param,
                    function (json, res) {
                        var _eventPoolStaffManageInfo = JSON.parse(json);
                        $that.eventPoolStaffManageInfo.total = _eventPoolStaffManageInfo.total;
                        $that.eventPoolStaffManageInfo.records = _eventPoolStaffManageInfo.records;
                        $that.eventPoolStaffManageInfo.eventPoolStaffs = _eventPoolStaffManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.eventPoolStaffManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openEditEventPoolStaffModel: function (_eventPoolStaff) {
                vc.emit('editEventPoolStaff', 'openEditEventPoolStaffModal', _eventPoolStaff);
            },
            _queryEventPoolStaffMethod: function () {
                $that._listEventPoolStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetEventPoolStaffMethod: function () {
                $that.eventPoolStaffManageInfo.conditions = {
                    eventId: '',
                    state: '',
                };
                $that._listEventPoolStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _acceptEvent: function (_eventPoolStaff) {
                vc.emit('editEventPoolStaff', 'openEditEventPoolStaffModal', _eventPoolStaff);
            },
            _openModel: function (_data, _title) {
                vc.emit('viewData', 'openEventViewDataModal', {
                    title: _title,
                    data: _data
                });
            },
            _detail: function (_eventPoolStaff) {
                vc.emit('eventDetail', 'openEventDetailModal', _eventPoolStaff);
            }
        }
    });
})(window.vc);
