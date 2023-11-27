/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            instrumentLogManageInfo: {
                instrumentLogs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    logId: '',
                    action: '',
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    communityId: '',
                }
            }
        },
        _initMethod: function () {
            $that._listInstrumentLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('instrumentLogManage', 'listInstrumentLog', function (_param) {
                $that._listInstrumentLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listInstrumentLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listInstrumentLogs: function (_page, _rows) {

                $that.instrumentLogManageInfo.conditions.page = _page;
                $that.instrumentLogManageInfo.conditions.row = _rows;
                $that.instrumentLogManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.instrumentLogManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/instrumentLog.listInstrumentLog',
                    param,
                    function (json, res) {
                        var _instrumentLogManageInfo = JSON.parse(json);
                        $that.instrumentLogManageInfo.total = _instrumentLogManageInfo.total;
                        $that.instrumentLogManageInfo.records = _instrumentLogManageInfo.records;
                        $that.instrumentLogManageInfo.instrumentLogs = _instrumentLogManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.instrumentLogManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryInstrumentLogMethod: function () {
                $that._listInstrumentLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetInstrumentLogMethod: function () {
                $that.instrumentLogManageInfo.conditions = {
                    logId: '',
                    action: '',
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                    communityId: '',
                }
                $that._listInstrumentLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openViewInstrumentLogSensorModel: function (_log) {
                vc.emit('showInstrumentLogSensor', 'openViewInstrumentLogSensorModal', _log);
            },
        }
    });
})(window.vc);
