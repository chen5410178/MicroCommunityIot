/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorMachineStaffInfo: {
                monitorMachinePersons: [],
                monitors:[],
                total: 0,
                records: 1,
                moreCondition: false,
                mmuId: '',
                conditions: {
                    machineName: '',
                    machineId: '',
                    personType: '',
                    personName: '',

                }
            }
        },
        _initMethod: function () {
            $that._listMonitorMachines();
            $that._listMonitorMachinePersons(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('monitorMachineStaff', 'listMonitorMachinePerson', function (_param) {
                $that._listMonitorMachinePersons(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorMachinePersons(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorMachinePersons: function (_page, _rows) {

                $that.monitorMachineStaffInfo.conditions.page = _page;
                $that.monitorMachineStaffInfo.conditions.row = _rows;
                $that.monitorMachineStaffInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.monitorMachineStaffInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitor.listMonitorMachinePerson',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorMachineStaffInfo.total = _json.total;
                        $that.monitorMachineStaffInfo.records = _json.records;
                        $that.monitorMachineStaffInfo.monitorMachinePersons = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorMachineStaffInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMonitorMachinePersonModal: function () {
                vc.jumpToPage('/#/pages/monitor/addMonitorMachineStaff?machineId='+$that.monitorMachineStaffInfo.conditions.machineId);
            },
            _openDeleteMonitorMachinePersonModel: function (_monitorMachinePerson) {
                vc.emit('deleteMonitorMachinePerson', 'openDeleteMonitorMachinePersonModal', _monitorMachinePerson);
            },
            _queryMonitorMachinePersonMethod: function () {
                $that._listMonitorMachinePersons(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            swatchMonitor:function(_monitor){
                $that.monitorMachineStaffInfo.conditions.machineId= _monitor.machineId;
                $that._listMonitorMachinePersons(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listMonitorMachines: function (_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                let _monitors = [
                    {
                        machineName:'全部',
                        machineId:''
                    }
                ];
                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        _json.data.forEach(item => {
                            _monitors.push(item);
                        });
                        $that.monitorMachineStaffInfo.monitors = _monitors;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

        }
    });
})(window.vc);
