/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lockMachineManageInfo: {
                lockMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    roomId: '',
                    roomName: '',
                    implBean: '',
                    communityId: '',
                }
            },
            lockMachineFactoryList: [],
        },
        _initMethod: function () {
            $that._listLockMachineFactorys();
            $that._listLockMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('lockMachineManage', 'listLockMachine', function (_param) {
                $that._listLockMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLockMachines(_currentPage, DEFAULT_ROWS);
            });
            vc.on('lockMachineManage', 'selectRoom', function(param) {
                $that.lockMachineManageInfo.conditions.roomId = param.roomId;
            });
        },
        methods: {
            _listLockMachines: function (_page, _rows) {
                if (!vc.getCurrentCommunity()) {
                    vc.toast("缺少小区信息");
                    return;
                }
                $that.lockMachineManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                $that.lockMachineManageInfo.conditions.page = _page;
                $that.lockMachineManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lockMachineManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lockMachine.listLockMachine',
                    param,
                    function (json, res) {
                        var _lockMachineManageInfo = JSON.parse(json);
                        $that.lockMachineManageInfo.total = _lockMachineManageInfo.total;
                        $that.lockMachineManageInfo.records = _lockMachineManageInfo.records;
                        $that.lockMachineManageInfo.lockMachines = _lockMachineManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lockMachineManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLockMachineModal: function () {
                vc.emit('addLockMachine', 'openAddLockMachineModal', {});
            },
            _openEditLockMachineModel: function (_lockMachine) {
                vc.emit('editLockMachine', 'openEditLockMachineModal', _lockMachine);
            },
            _openDeleteLockMachineModel: function (_lockMachine) {
                vc.emit('deleteLockMachine', 'openDeleteLockMachineModal', _lockMachine);
            },
            _queryLockMachineMethod: function () {
                $that._listLockMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLockMachineMethod: function () {
                $that.lockMachineManageInfo.conditions = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    roomId: '',
                    roomName: '',
                    implBean: '',
                    communityId: '',
                };
                $that._listLockMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.lockMachineManageInfo.moreCondition) {
                    $that.lockMachineManageInfo.moreCondition = false;
                } else {
                    $that.lockMachineManageInfo.moreCondition = true;
                }
            },
            _listLockMachineFactorys: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100
                    }
                };
                //发送get请求
                vc.http.apiGet('/lockMachineFactory.listLockMachineFactory',
                    param,
                    function (json, res) {
                        var _lockMachineFactoryManageInfo = JSON.parse(json);
                        $that.lockMachineFactoryList = _lockMachineFactoryManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            selectRoom: function() {
                vc.emit('roomTree', 'openRoomTree', {
                    callName: 'lockMachineManage'
                })
            },
        }
    });
})(window.vc);
