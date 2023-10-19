/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorMachineManageInfo: {
                monitorMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    maId: '',
                    protocol: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    state: '',
                }
            },
            protocolList: [],
            monitorAreaList: [],
        },
        _initMethod: function () {
            $that._listMonitorMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.getDict('monitor_machine', 'protocol', function (_data) {
                $that.protocolList = _data;
            });
            $that._listMonitorAreas();
        },
        _initEvent: function () {
            vc.on('monitorMachineManage', 'listMonitorMachine', function (_param) {
                $that._listMonitorMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorMachines: function (_page, _rows) {
                $that.monitorMachineManageInfo.conditions.page = _page;
                $that.monitorMachineManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.monitorMachineManageInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        var _monitorMachineManageInfo = JSON.parse(json);
                        $that.monitorMachineManageInfo.total = _monitorMachineManageInfo.total;
                        $that.monitorMachineManageInfo.records = _monitorMachineManageInfo.records;
                        $that.monitorMachineManageInfo.monitorMachines = _monitorMachineManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorMachineManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMonitorMachineModal: function () {
                vc.emit('addMonitorMachine', 'openAddMonitorMachineModal', {});
            },
            _openEditMonitorMachineModel: function (_monitorMachine) {
                vc.emit('editMonitorMachine', 'openEditMonitorMachineModal', _monitorMachine);
            },
            _openDeleteMonitorMachineModel: function (_monitorMachine) {
                vc.emit('deleteMonitorMachine', 'openDeleteMonitorMachineModal', _monitorMachine);
            },
            _queryMonitorMachineMethod: function () {
                $that._listMonitorMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMonitorMachineMethod: function () {
                $that.monitorMachineManageInfo.conditions = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    maId: '',
                    protocol: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    state: '',
                }
                $that._listMonitorMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.monitorMachineManageInfo.moreCondition) {
                    $that.monitorMachineManageInfo.moreCondition = false;
                } else {
                    $that.monitorMachineManageInfo.moreCondition = true;
                }
            },
            _listMonitorAreas: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorArea.listMonitorArea',
                    param,
                    function (json, res) {
                        var _monitorAreaManageInfo = JSON.parse(json);
                        $that.monitorAreaList = _monitorAreaManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
