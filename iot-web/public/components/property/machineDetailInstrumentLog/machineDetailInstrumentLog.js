/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailInstrumentLogInfo: {
                logs: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailInstrumentLog', 'switch', function (_data) {
                $that.machineDetailInstrumentLogInfo.machineId = _data.machineId;
                $that._loadMachineDetailInstrumentLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailInstrumentLog', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailInstrumentLogData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailInstrumentLogData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailInstrumentLogInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/instrumentLog.listInstrumentLog',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailInstrumentLogInfo.logs = _machineInfo.data;
                        vc.emit('machineDetailInstrumentLog', 'paginationPlus', 'init', {
                            total: _machineInfo.records,
                            dataCount: _machineInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            _openViewInstrumentLogSensorModel: function (_log) {
                vc.emit('showInstrumentLogSensor', 'openViewInstrumentLogSensorModal', _log);
            },
        }
    });
})(window.vc);