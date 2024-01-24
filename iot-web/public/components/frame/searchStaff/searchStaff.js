(function(vc) {
    vc.extends({
        data: {
            searchStaffInfo: {
                staffs: [],
                _currentStaffName: '',
                orgId: '',
                staff:{}
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('searchStaff', 'openSearchStaffModel', function(_param) {
                console.log("打开定位员工界面")
                $('#searchStaffModel').modal('show');
                $that._refreshSearchStaffData();
                if (_param.hasOwnProperty('orgId')) {
                    $that.searchStaffInfo.orgId = _param.orgId;
                }
                $that.searchStaffInfo.staff = _param;
                $that._loadAllStaffInfo(1, 10);
            });

            vc.on('searchStaff', 'paginationPlus', 'page_event', function(_currentPage) {
                $that._loadAllStaffInfo(_currentPage, 10);
            });
        },
        methods: {
            _loadAllStaffInfo: function(_page, _rows, _staffName) {
                var param = {
                    params: {
                        page: _page,
                        row: _rows,
                        staffName: _staffName,
                        orgId: $that.searchStaffInfo.orgId
                    }
                };

                //发送get请求
                vc.http.apiGet('/query.staff.infos',
                    param,
                    function(json) {
                        let _staffInfo = JSON.parse(json);
                        $that.searchStaffInfo.staffs = _staffInfo.data;
                        vc.emit('searchStaff', 'paginationPlus', 'init', {
                            total: _staffInfo.records,
                            dataCount: _staffInfo.total,
                            currentPage: _page
                        });
                    },
                    function() {
                        console.log('请求失败处理');
                    }
                );
            },
            chooseStaff: function(_staff) {
                //vc.emit('privilegeStaffInfo','chooseStaff',_staff);
                $that.searchStaffInfo.staff.staffId = _staff.userId;
                $that.searchStaffInfo.staff.staffName = _staff.userName;
                vc.emit('staffPrivilege', '_loadStaffPrivileges', {
                    staffId: _staff.userId
                });
                $('#searchStaffModel').modal('hide');
            },
            searchStaffs: function() {
                $that._loadAllStaffInfo(1, 10, $that.searchStaffInfo._currentStaffName);
            },
            _refreshSearchStaffData: function() {
                $that.searchStaffInfo._currentStaffName = "";
            }
        }

    });
})(window.vc);