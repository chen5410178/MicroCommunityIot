/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorRecordInfo: {
                videoRecords: [],
                monitors: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mmuId: '',
                conditions: {
                    machineName: '',
                    machineId: '',
                    personType: '2002',
                    personName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listMonitorMachines();
            $that._listMonitorRecords(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('monitorRecord', 'listMonitorMachinePerson', function (_param) {
                $that._listMonitorRecords(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorRecords(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorRecords: function (_page, _rows) {

                $that.monitorRecordInfo.conditions.page = _page;
                $that.monitorRecordInfo.conditions.row = _rows;
                $that.monitorRecordInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.monitorRecordInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorRecord',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorRecordInfo.total = _json.total;
                        $that.monitorRecordInfo.records = _json.records;
                        $that.monitorRecordInfo.videoRecords = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorRecordInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _queryMonitorMachinePersonMethod: function () {
                $that._listMonitorRecords(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            swatchMonitor: function (_monitor) {
                $that.monitorRecordInfo.conditions.machineId = _monitor.machineId;
                $that._listMonitorRecords(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listMonitorMachines: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                let _monitors = [];
                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        _json.data.forEach(item => {
                            _monitors.push(item);
                        });

                        $that.monitorRecordInfo.monitors = _monitors;
                        if (_monitors && _monitors.length > 0) {
                            $that.swatchMonitor(_monitors[0]);
                        }

                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openPlayVideo:function(_monitorMachine){
                _monitorMachine.machineId = $that.monitorRecordInfo.conditions.machineId;
                vc.emit('playRecordMonitorVideo', 'openPlayMonitorVideoModal', _monitorMachine);
            },

        }
    });
})(window.vc);
