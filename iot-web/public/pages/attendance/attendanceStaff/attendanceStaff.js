/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            attendanceStaffInfo: {
                staffs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                csId: '',
                machines: [],
                conditions: {
                    machineId: '',
                    staffId: '',
                    staffNameLike: ''
                }
            }
        },
        _initMethod: function () {
            $that._loadAttendanceMachines();
        },
        _initEvent: function () {
            vc.on('attendanceStaff', 'listAttendanceStaff', function (_param) {
                $that._listAttendanceStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAttendanceStaffs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAttendanceStaffs: function (_page, _rows) {
                $that.attendanceStaffInfo.conditions.page = _page;
                $that.attendanceStaffInfo.conditions.row = _rows;
                $that.attendanceStaffInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.attendanceStaffInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceStaff',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.attendanceStaffInfo.total = _json.total;
                        $that.attendanceStaffInfo.records = _json.records;
                        $that.attendanceStaffInfo.staffs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.attendanceStaffInfo.records,
                            dataCount: $that.attendanceStaffInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddAttendanceStaffModal: function () {
                vc.jumpToPage('/#/pages/attendance/addAttendanceStaff?machineId=' + $that.attendanceStaffInfo.conditions.machineId);
            },
            _openDeleteAttendanceStaffModel: function (_attendanceStaff) {
                vc.emit('deleteAttendanceStaff', 'openDeleteAttendanceStaffModal', _attendanceStaff);
            },
            //查询
            _queryAttendanceStaffMethod: function () {
                $that._listAttendanceStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetAttendanceStaffMethod: function () {
                $that.attendanceStaffInfo.conditions.staffNameLike = "";
                $that._listAttendanceStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.attendanceStaffInfo.moreCondition) {
                    $that.attendanceStaffInfo.moreCondition = false;
                } else {
                    $that.attendanceStaffInfo.moreCondition = true;
                }
            },
            swatchMachine: function (_attendanceClass) {
                $that.attendanceStaffInfo.conditions.machineId = _attendanceClass.machineId;
                $that._listAttendanceStaffs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _loadAttendanceMachines: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.attendanceStaffInfo.machines = _json.data;
                        if (_json.data && _json.data.length > 0) {
                            $that.swatchMachine(_json.data[0]);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            showImg: function (e) {
                if (!e) {
                    e = '/img/noPhoto.jpg';
                }
                vc.emit('viewImage', 'showImage', {url: e});
            },
        }
    });
})(window.vc);