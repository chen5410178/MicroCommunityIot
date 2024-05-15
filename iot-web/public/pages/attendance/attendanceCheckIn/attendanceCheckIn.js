/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            attendanceCheckInInfo: {
                checkIns: [],
                machines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                inoutId: '',
                conditions: {
                    machineId: '',
                    staffName: '',
                    openTypeCd: '',
                    tel: '',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function() {
            $that._listMachines();
            $that._listAttendanceCheckIns(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.attendanceCheckInInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.attendanceCheckInInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function() {

            vc.on('attendanceCheckIn', 'listAttendanceCheckIn', function(_param) {
                $that._listAttendanceCheckIns(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAttendanceCheckIns(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAttendanceCheckIns: function(_page, _rows) {

                $that.attendanceCheckInInfo.conditions.page = _page;
                $that.attendanceCheckInInfo.conditions.row = _rows;
                $that.attendanceCheckInInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.attendanceCheckInInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceCheckin',
                    param,
                    function(json, res) {
                        var _json = JSON.parse(json);
                        $that.attendanceCheckInInfo.total = _json.total;
                        $that.attendanceCheckInInfo.records = _json.records;
                        $that.attendanceCheckInInfo.checkIns = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.attendanceCheckInInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _queryAttendanceCheckInMethod: function() {
                $that._listAttendanceCheckIns(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },

            _listMachines: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.attendanceCheckInInfo.accessControls = [];

                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.attendanceCheckInInfo.machines = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },


        }
    });
})(window.vc);