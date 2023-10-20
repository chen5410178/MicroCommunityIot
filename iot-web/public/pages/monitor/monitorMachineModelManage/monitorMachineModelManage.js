/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorMachineModelManageInfo: {
                monitorMachineModels: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mmmId: '',
                conditions: {
                    mmmId: '',
                    machineId: '',
                    modelId: '',
                    communityId: '',
                    maId: '',
                    machineName: '',
                    locationName: ''
                }
            },
            monitorModelList: [],
            monitorAreaList: []
        },
        _initMethod: function () {
            $that.listMonitorModels();
            $that.listMonitorAreas();
            $that._listMonitorMachineModels(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('monitorMachineModelManage', 'listMonitorMachineModel', function (_param) {
                $that._listMonitorMachineModels(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorMachineModels(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorMachineModels: function (_page, _rows) {

                $that.monitorMachineModelManageInfo.conditions.page = _page;
                $that.monitorMachineModelManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.monitorMachineModelManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitorMachineModel.listMonitorMachineModel',
                    param,
                    function (json, res) {
                        var _monitorMachineModelManageInfo = JSON.parse(json);
                        $that.monitorMachineModelManageInfo.total = _monitorMachineModelManageInfo.total;
                        $that.monitorMachineModelManageInfo.records = _monitorMachineModelManageInfo.records;
                        $that.monitorMachineModelManageInfo.monitorMachineModels = _monitorMachineModelManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorMachineModelManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMonitorMachineModelModal: function () {
                vc.jumpToPage('/#/pages/monitor/addMonitorMachineModel');
            },
            _openDeleteMonitorMachineModelModel: function (_monitorMachineModel) {
                vc.emit('deleteMonitorMachineModel', 'openDeleteMonitorMachineModelModal', _monitorMachineModel);
            },
            _queryMonitorMachineModelMethod: function () {
                $that._listMonitorMachineModels(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMonitorMachineModelMethod: function () {
                $that.monitorMachineModelManageInfo.conditions = {
                    mmmId: '',
                    machineId: '',
                    modelId: $that.monitorMachineModelManageInfo.conditions.modelId,
                    communityId: '',
                    maId: '',
                    machineName: '',
                    locationName: ''
                };
                $that._listMonitorMachineModels(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            switchMonitorModel: function (_monitorModel) {
                $that.monitorMachineModelManageInfo.conditions.modelId = _monitorModel.modelId;
                $that._listMonitorMachineModels(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            listMonitorModels: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                    }
                };
                //发送get请求
                vc.http.apiGet('/monitorModel.listMonitorModel',
                    param,
                    function (json, res) {
                        var _monitorModelManageInfo = JSON.parse(json);
                        $that.monitorModelList = [{
                            modelName: '监控模型',
                            modelId: ''
                        }];
                        _monitorModelManageInfo.data.forEach(item => {
                            $that.monitorModelList.push(item);
                        })
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            listMonitorAreas: function () {
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
