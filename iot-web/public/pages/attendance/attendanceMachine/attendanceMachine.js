/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            attendanceMachineInfo: {
                attendanceMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineCode: '',
                    machineName: '',
                    classesName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listAttendanceMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('attendanceMachine', 'listAttendanceMachine', function (_param) {
                $that._listAttendanceMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAttendanceMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAttendanceMachines: function (_page, _rows) {

                $that.attendanceMachineInfo.conditions.page = _page;
                $that.attendanceMachineInfo.conditions.row = _rows;
                $that.attendanceMachineInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.attendanceMachineInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.attendanceMachineInfo.total = _json.total;
                        $that.attendanceMachineInfo.records = _json.records;
                        $that.attendanceMachineInfo.attendanceMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.attendanceMachineInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddAttendanceMachineModal: function () {
                vc.emit('addAttendanceMachine', 'openAddAttendanceMachineModal', {});
            },
            _openEditAttendanceMachineModel: function (_attendanceMachine) {
                vc.emit('editAttendanceMachine', 'openEditAttendanceMachineModal', _attendanceMachine);
            },
            _openDeleteAttendanceMachineModel: function (_attendanceMachine) {
                vc.emit('deleteAttendanceMachine', 'openDeleteAttendanceMachineModal', _attendanceMachine);
            },
            _queryAttendanceMachineMethod: function () {
                $that._listAttendanceMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.attendanceMachineInfo.moreCondition) {
                    $that.attendanceMachineInfo.moreCondition = false;
                } else {
                    $that.attendanceMachineInfo.moreCondition = true;
                }
            },
            _viewAccessControlVideo:function(_attendanceMachine){
                vc.emit('playMonitorVideo', 'openPlayMonitorVideoModal', {
                    machineId:_attendanceMachine.monitorId,
                    machineName:_attendanceMachine.monitorName,
                    communityId:_attendanceMachine.communityId
                });
            }
        }
    });
})(window.vc);
