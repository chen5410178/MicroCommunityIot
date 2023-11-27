/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailInstrumentSensorInfo: {
                sensors: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailInstrumentSensor', 'switch', function (_data) {
                $that.machineDetailInstrumentSensorInfo.machineId = _data.machineId;
                $that._loadMachineDetailInstrumentSensorData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailInstrumentSensor', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailInstrumentSensorData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailInstrumentSensorData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailInstrumentSensorInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/instrument.listInstrument',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailInstrumentSensorInfo.sensors = _machineInfo.data[0].instrumentSensorList;
                        vc.emit('machineDetailInstrumentSensor', 'paginationPlus', 'init', {
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