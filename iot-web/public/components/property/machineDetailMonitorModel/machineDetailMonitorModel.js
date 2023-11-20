/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailMonitorModelInfo: {
                models: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailMonitorModel', 'switch', function (_data) {
                $that.machineDetailMonitorModelInfo.machineId = _data.machineId;
                $that._loadMachineDetailMonitorModelData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailMonitorModel', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailMonitorModelData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailMonitorModelData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailMonitorModelInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorMachineModel.listMonitorMachineModel',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailMonitorModelInfo.models = _machineInfo.data;
                        vc.emit('machineDetailMonitorModel', 'paginationPlus', 'init', {
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
        }
    });
})(window.vc);