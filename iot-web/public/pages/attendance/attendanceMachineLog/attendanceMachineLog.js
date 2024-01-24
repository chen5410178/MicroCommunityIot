/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            attendanceMachineLogInfo: {
                attendanceMachineLogs: [],
                machines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    machineId: '',
                    logAction: '',
                    state: '',
                    userName: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function() {
            $that._listAttendanceMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listMachines();
            vc.initDateTime('queryStartTime',function(_value){
                $that.attendanceMachineLogInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.attendanceMachineLogInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function() {

            vc.on('attendanceMachineLog', 'listAttendanceMachineLog', function(_param) {
                $that._listAttendanceMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAttendanceMachineLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAttendanceMachineLogs: function(_page, _rows) {

                $that.attendanceMachineLogInfo.conditions.page = _page;
                $that.attendanceMachineLogInfo.conditions.row = _rows;
                $that.attendanceMachineLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.attendanceMachineLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceMachineLog',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.attendanceMachineLogInfo.total = _json.total;
                        $that.attendanceMachineLogInfo.records = _json.records;
                        $that.attendanceMachineLogInfo.attendanceMachineLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.attendanceMachineLogInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openViewAttendanceMachineLogModel: function(_log) {
                vc.emit('showAttendanceMachineLog', 'openShowAttendanceMachineLogModal', _log);
            },

            _queryAttendanceMachineLogMethod: function() {
                $that._listAttendanceMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _listMachines: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.attendanceMachineLogInfo.machines = [{
                    machineId: '',
                    machineName: '全部'
                }]

                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        _json.data.forEach(item => {
                            $that.attendanceMachineLogInfo.machines.push(item);
                        })
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchMachine: function(_accessControl) {
                $that.attendanceMachineLogInfo.conditions.machineId = _accessControl.machineId;
                $that._listAttendanceMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },


        }
    });
})(window.vc);