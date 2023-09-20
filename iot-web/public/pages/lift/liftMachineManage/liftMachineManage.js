/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineManageInfo: {
                liftMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    brand: '',
                    locationName: '',
                    factoryId: '',
                    communityId: '',
                    state: '',
                    curLayer: '',
                }
            },
            liftMachineFactoryList: [],
        },
        _initMethod: function () {
            vc.component._listLiftMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listLiftMachineFactorys();
        },
        _initEvent: function () {

            vc.on('liftMachineManage', 'listLiftMachine', function (_param) {
                vc.component._listLiftMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listLiftMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachines: function (_page, _rows) {

                vc.component.liftMachineManageInfo.conditions.page = _page;
                vc.component.liftMachineManageInfo.conditions.row = _rows;
                vc.component.liftMachineManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: vc.component.liftMachineManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('liftMachine.listLiftMachine',
                    param,
                    function (json, res) {
                        var _liftMachineManageInfo = JSON.parse(json);
                        vc.component.liftMachineManageInfo.total = _liftMachineManageInfo.total;
                        vc.component.liftMachineManageInfo.records = _liftMachineManageInfo.records;
                        vc.component.liftMachineManageInfo.liftMachines = _liftMachineManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.liftMachineManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listLiftMachineFactorys: function () {
                var param = {
                    params: {
                        page: -1,
                        row: 100
                    }
                };
                //发送get请求
                vc.http.apiGet('liftMachineFactory.listLiftMachineFactory',
                    param,
                    function (json, res) {
                        var _liftMachineFactoryManageInfo = JSON.parse(json);
                        vc.component.liftMachineFactoryList = _liftMachineFactoryManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _switchLiftState: function (_liftMachine) {
                let curUserInfo = JSON.parse(window.localStorage.getItem('/nav/getUserInfo'));
                _liftMachine.userId = curUserInfo.userId;
                _liftMachine.userName = curUserInfo.name;

                if (_liftMachine.state === '1001') {
                    _liftMachine.logAction = 'stopping';
                } if (_liftMachine.state === '2002') {
                    _liftMachine.logAction = 'running';
                } else {
                    _liftMachine.logAction = 'unknown';
                }

                vc.http.apiPost(
                    'liftMachine.switchLiftState',
                    JSON.stringify(_liftMachine), {
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
            _openReservationLiftMachineModel: function (_liftMachine) {
                vc.emit('reservationLiftMachine', 'openReservationLiftMachineModal', _liftMachine);
            },
            _openAddLiftMachineModal: function () {
                vc.emit('addLiftMachine', 'openAddLiftMachineModal', {});
            },
            _openEditLiftMachineModel: function (_liftMachine) {
                vc.emit('editLiftMachine', 'openEditLiftMachineModal', _liftMachine);
            },
            _openDeleteLiftMachineModel: function (_liftMachine) {
                vc.emit('deleteLiftMachine', 'openDeleteLiftMachineModal', _liftMachine);
            },
            _queryLiftMachineMethod: function () {
                vc.component._listLiftMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if (vc.component.liftMachineManageInfo.moreCondition) {
                    vc.component.liftMachineManageInfo.moreCondition = false;
                } else {
                    vc.component.liftMachineManageInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
