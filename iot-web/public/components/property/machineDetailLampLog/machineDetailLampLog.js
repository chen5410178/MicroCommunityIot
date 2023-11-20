/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailLampLogInfo: {
                logs: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailLampLog', 'switch', function (_data) {
                $that.machineDetailLampLogInfo.machineId = _data.machineId;
                $that._loadMachineDetailLampLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailLampLog', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailLampLogData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailLampLogData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailLampLogInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/lampMachineLog.listLampMachineLog',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailLampLogInfo.logs = _machineInfo.data;
                        vc.emit('machineDetailLampLog', 'paginationPlus', 'init', {
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
            _openViewLampMachineLogModel: function (_log) {
                vc.emit('showLampMachineLog', 'openShowLampMachineLogModal', _log);
            },
        }
    });
})(window.vc);