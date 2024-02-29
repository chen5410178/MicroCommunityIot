/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            workLicenseLogInfo: {
                workLicenseLogs: [],
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
            $that._listWorkLicenseLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listWorkLicenseMachines();
            vc.initDateTime('queryStartTime',function(_value){
                $that.workLicenseLogInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.workLicenseLogInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function() {

            vc.on('workLicenseLog', 'listWorkLicenseLog', function(_param) {
                $that._listWorkLicenseLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listWorkLicenseLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listWorkLicenseLogs: function(_page, _rows) {

                $that.workLicenseLogInfo.conditions.page = _page;
                $that.workLicenseLogInfo.conditions.row = _rows;
                $that.workLicenseLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.workLicenseLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicenseLog',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.workLicenseLogInfo.total = _json.total;
                        $that.workLicenseLogInfo.records = _json.records;
                        $that.workLicenseLogInfo.workLicenseLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.workLicenseLogInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openViewWorkLicenseLogModel: function(_log) {
                vc.emit('showWorkLicenseLog', 'openShowWorkLicenseLogModal', _log);
            },

            _queryWorkLicenseLogMethod: function() {
                $that._listWorkLicenseLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _listWorkLicenseMachines: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                    }
                };
                $that.workLicenseLogInfo.machines = [{
                    machineId: '',
                    machineName: '全部'
                }]

                //发送get请求
                vc.http.apiGet('/workLicense.listWorkLicenseMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        _json.data.forEach(item => {
                            $that.workLicenseLogInfo.machines.push(item);
                        })
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchAccessControl: function(_accessControl) {
                $that.workLicenseLogInfo.conditions.machineId = _accessControl.machineId;
                $that._listWorkLicenseLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },


        }
    });
})(window.vc);