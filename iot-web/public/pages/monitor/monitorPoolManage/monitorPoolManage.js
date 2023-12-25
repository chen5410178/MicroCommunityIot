/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorPoolManageInfo: {
                monitorPools: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mpId: '',
                conditions: {
                    mpId: '',
                    machineId: '',
                    machineCode: '',
                    modelId: '',
                    monitorType: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    queryStartTime: '',
                    queryEndTime: ''
                }
            },
            monitorTypeList: [],
            monitorMachineList: [],
            monitorModelList: []
        },
        _initMethod: function () {
            vc.getDict('monitor_pool', 'monitor_type', function (_data) {
                $that.monitorTypeList = _data;
            });
            $that._listMonitorMachines();
            $that._listMonitorModels()
            $that._listMonitorPools(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.monitorPoolManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.monitorPoolManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('monitorPoolManage', 'listMonitorPool', function (_param) {
                $that._listMonitorPools(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorPools(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorPools: function (_page, _rows) {
                $that.monitorPoolManageInfo.conditions.page = _page;
                $that.monitorPoolManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.monitorPoolManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitorPool.listMonitorPool',
                    param,
                    function (json, res) {
                        var _monitorPoolManageInfo = JSON.parse(json);
                        $that.monitorPoolManageInfo.total = _monitorPoolManageInfo.total;
                        $that.monitorPoolManageInfo.records = _monitorPoolManageInfo.records;
                        $that.monitorPoolManageInfo.monitorPools = _monitorPoolManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorPoolManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryMonitorPoolMethod: function () {
                $that._listMonitorPools(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMonitorPoolMethod: function () {
                $that.monitorPoolManageInfo.conditions = {
                    mpId: '',
                    machineId: '',
                    machineCode: '',
                    modelId: '',
                    monitorType: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    queryStartTime: '',
                    queryEndTime: ''
                };
                $that._listMonitorPools(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openModel: function (_data, _title) {
                vc.emit('viewData', 'openEventViewDataModal', {
                    title: _title,
                    data: _data
                });
            },
            _listMonitorMachines: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        var _monitorMachineManageInfo = JSON.parse(json);
                        $that.monitorMachineList = _monitorMachineManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listMonitorModels: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorModel.listMonitorModel',
                    param,
                    function (json, res) {
                        var _monitorModelManageInfo = JSON.parse(json);
                        $that.monitorModelList = _monitorModelManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
