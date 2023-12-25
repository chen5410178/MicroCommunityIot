/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lampMachineLogManageInfo: {
                lampMachineLogs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    logId: '',
                    machineId: '',
                    communityId: '',
                    logAction: '',
                    state: '',
                    personId: '',
                    createTime: '',
                    queryStartTime: '',
                    queryEndTime: ''
                },
                lampMachineList: [],
                operatePersonList: [],
                operateTypes: [],
                reqStateList: [],
            }
        },
        _initMethod: function () {
            vc.getDict('lamp_machine_operation', 'type', function (_data) {
                $that.lampMachineLogManageInfo.operateTypes = _data
            });
            vc.getDict('lift_machine_log', 'state', function (_data) {
                $that.lampMachineLogManageInfo.reqStateList = _data
            });
            $that._listLampMachine();
            $that._listOperatePerson();
            $that._listLampMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.lampMachineLogManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.lampMachineLogManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('lampMachineLogManage', 'listLampMachineLog', function (_param) {
                $that._listLampMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLampMachineLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLampMachineLogs: function (_page, _rows) {

                $that.lampMachineLogManageInfo.conditions.page = _page;
                $that.lampMachineLogManageInfo.conditions.row = _rows;
                $that.lampMachineLogManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.lampMachineLogManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lampMachineLog.listLampMachineLog',
                    param,
                    function (json, res) {
                        var _lampMachineLogManageInfo = JSON.parse(json);
                        $that.lampMachineLogManageInfo.total = _lampMachineLogManageInfo.total;
                        $that.lampMachineLogManageInfo.records = _lampMachineLogManageInfo.records;
                        $that.lampMachineLogManageInfo.lampMachineLogs = _lampMachineLogManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lampMachineLogManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLampMachineLogMethod: function () {
                $that._listLampMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLampMachineLogMethod: function () {
                $that.lampMachineLogManageInfo.conditions = {
                    logId: '',
                    machineId: '',
                    logAction: '',
                    state: '',
                    personId: '',
                    createTime: '',
                    queryStartTime: '',
                    queryEndTime: ''
                };
                $that._listLampMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _selectOperationDate: function () {
                vc.initDate('operationTime', function(_value) {
                    $that.lampMachineLogManageInfo.conditions.createTime = _value;
                });
            },
            _moreCondition: function () {
                if ($that.lampMachineLogManageInfo.moreCondition) {
                    $that.lampMachineLogManageInfo.moreCondition = false;
                } else {
                    $that.lampMachineLogManageInfo.moreCondition = true;
                }
            },
            _openViewLampMachineLogModel: function (_log) {
                vc.emit('showLampMachineLog', 'openShowLampMachineLogModal', _log);
            },
            _listLampMachine: function () {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        page: -1,
                        row: 100
                    }
                }
                vc.http.apiGet('/lampMachine.listLampMachine',
                    param,
                    function (json, res) {
                        var _lampMachineList = JSON.parse(json);
                        $that.lampMachineLogManageInfo.lampMachineList = _lampMachineList.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listOperatePerson: function () {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId
                    }
                }
                vc.http.apiGet('/user.listCommunityStaffsByCommunityId',
                    param,
                    function (json, res) {
                        var _listOperatePerson = JSON.parse(json);
                        $that.lampMachineLogManageInfo.operatePersonList = _listOperatePerson.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
