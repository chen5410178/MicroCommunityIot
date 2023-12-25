/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lampMachineOperationManageInfo: {
                lampMachineOperations: [],
                total: 0,
                records: 1,
                moreCondition: false,
                lmoId: '',
                conditions: {
                    lmoId: '',
                    machineId: '',
                    personId: '',
                    type: '',
                    curCommunityId: vc.getCurrentCommunity().communityId,
                    queryStartTime: '',
                    queryEndTime: ''
                },
                operateTypes: [],
                lampMachineList: [],
                operatePersonList: [],
            }
        },
        _initMethod: function () {
            vc.getDict('lamp_machine_operation', 'type', function (_data) {
                $that.lampMachineOperationManageInfo.operateTypes = _data
            });
            $that._listLampMachine();
            $that._listOperatePerson();
            $that._listLampMachineOperations(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.lampMachineOperationManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.lampMachineOperationManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('lampMachineOperationManage', 'listLampMachineOperation', function (_param) {
                $that._listLampMachineOperations(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLampMachineOperations(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLampMachineOperations: function (_page, _rows) {
                $that.lampMachineOperationManageInfo.conditions.page = _page;
                $that.lampMachineOperationManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lampMachineOperationManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lampMachineOperation.listLampMachineOperation',
                    param,
                    function (json, res) {
                        var _lampMachineOperationManageInfo = JSON.parse(json);
                        $that.lampMachineOperationManageInfo.total = _lampMachineOperationManageInfo.total;
                        $that.lampMachineOperationManageInfo.records = _lampMachineOperationManageInfo.records;
                        $that.lampMachineOperationManageInfo.lampMachineOperations = _lampMachineOperationManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lampMachineOperationManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLampMachineOperationMethod: function () {
                $that._listLampMachineOperations(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLampMachineOperationMethod: function () {
                $that.lampMachineOperationManageInfo.conditions = {
                    lmoId: '',
                    machineId: '',
                    personId: '',
                    type: '',
                    curCommunityId: vc.getCurrentCommunity().communityId,
                    queryStartTime: '',
                    queryEndTime: ''
                };
                $that._listLampMachineOperations(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listLampMachine: function () {
                let param = {
                    params: {
                        communityId: $that.lampMachineOperationManageInfo.conditions.curCommunityId,
                        page: -1,
                        row: 100
                    }
                }
                vc.http.apiGet('/lampMachine.listLampMachine',
                    param,
                    function (json, res) {
                        var _lampMachineList = JSON.parse(json);
                        $that.lampMachineOperationManageInfo.lampMachineList = _lampMachineList.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listOperatePerson: function () {
                let param = {
                    params: {
                        communityId: $that.lampMachineOperationManageInfo.conditions.curCommunityId
                    }
                }
                vc.http.apiGet('/user.listCommunityStaffsByCommunityId',
                    param,
                    function (json, res) {
                        var _listOperatePerson = JSON.parse(json);
                        $that.lampMachineOperationManageInfo.operatePersonList = _listOperatePerson.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);
