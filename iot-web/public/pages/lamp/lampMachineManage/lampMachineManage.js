/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lampMachineManageInfo: {
                lampMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    factoryId: '',
                    communityId: '',
                    state: '',
                },
                lampMachineFactoryList: [],
                lampMachineStateList: [],
            }
        },
        _initMethod: function () {
            $that._listLampMachineFactorys();
            vc.getDict('lamp_machine', 'state', function (_data) {
                $that.lampMachineManageInfo.lampMachineStateList = _data;
            })
            $that._listLampMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('lampMachineManage', 'listLampMachine', function (_param) {
                $that._listLampMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLampMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLampMachines: function (_page, _rows) {

                $that.lampMachineManageInfo.conditions.page = _page;
                $that.lampMachineManageInfo.conditions.row = _rows;
                $that.lampMachineManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.lampMachineManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lampMachine.listLampMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.lampMachineManageInfo.total = _json.total;
                        $that.lampMachineManageInfo.records = _json.records;
                        $that.lampMachineManageInfo.lampMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lampMachineManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLampMachineModal: function () {
                vc.emit('addLampMachine', 'openAddLampMachineModal', {});
            },
            _openEditLampMachineModel: function (_lampMachine) {
                vc.emit('editLampMachine', 'openEditLampMachineModal', _lampMachine);
            },
            _openDeleteLampMachineModel: function (_lampMachine) {
                vc.emit('deleteLampMachine', 'openDeleteLampMachineModal', _lampMachine);
            },
            _queryLampMachineMethod: function () {
                $that._listLampMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLampMachineMethod: function () {
                $that.lampMachineManageInfo.conditions = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    locationName: '',
                    factoryId: '',
                    communityId: '',
                    state: '',
                };
                $that._listLampMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.lampMachineManageInfo.moreCondition) {
                    $that.lampMachineManageInfo.moreCondition = false;
                } else {
                    $that.lampMachineManageInfo.moreCondition = true;
                }
            },
            _listLampMachineFactorys: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100,
                    }
                };

                //发送get请求
                vc.http.apiGet('/lampMachineFactory.listLampMachineFactory',
                    param,
                    function (json, res) {
                        var _lampMachineFactory = JSON.parse(json);
                        $that.lampMachineManageInfo.lampMachineFactoryList = _lampMachineFactory.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _switchLampState: function (_lampMachine) {
                let curUserInfo = JSON.parse(window.localStorage.getItem('/nav/getUserInfo'));
                _lampMachine.userId = curUserInfo.userId;
                _lampMachine.userName = curUserInfo.name;

                if (_lampMachine.state === '1001') {
                    _lampMachine.logAction = 'close';
                } if (_lampMachine.state === '1002') {
                    _lampMachine.logAction = 'open';
                } else {
                    _lampMachine.logAction = 'unknown';
                }

                vc.http.apiPost(
                    '/lampMachine.switchLampState',
                    JSON.stringify(_lampMachine), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            _openSetLampMachineUsageTimeModal: function () {
                vc.jumpToPage('/#/pages/lamp/lampMachineOpenCloseManage');
            },
            _toLampDetail: function (_machine) {
                vc.jumpToPage('/#/pages/lamp/lampMachineDetail?machineId=' + _machine.machineId);
            }
        }
    });
})(window.vc);
