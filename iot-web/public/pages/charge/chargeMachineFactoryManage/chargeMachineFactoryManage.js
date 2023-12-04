/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeMachineFactoryManageInfo: {
                chargeMachineFactorys: [],
                total: 0,
                records: 1,
                moreCondition: false,
                factoryId: '',
                conditions: {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                }
            }
        },
        _initMethod: function () {
            $that._listChargeMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('chargeMachineFactoryManage', 'listChargeMachineFactory', function (_param) {
                $that._listChargeMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listChargeMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachineFactorys: function (_page, _rows) {
                $that.chargeMachineFactoryManageInfo.conditions.page = _page;
                $that.chargeMachineFactoryManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.chargeMachineFactoryManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineFactory',
                    param,
                    function (json, res) {
                        var _chargeMachineFactoryManageInfo = JSON.parse(json);
                        $that.chargeMachineFactoryManageInfo.total = _chargeMachineFactoryManageInfo.total;
                        $that.chargeMachineFactoryManageInfo.records = _chargeMachineFactoryManageInfo.records;
                        $that.chargeMachineFactoryManageInfo.chargeMachineFactorys = _chargeMachineFactoryManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeMachineFactoryManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeMachineFactoryModal: function () {
                vc.emit('addChargeMachineFactory', 'openAddChargeMachineFactoryModal', {});
            },
            _openEditChargeMachineFactoryModel: function (_chargeMachineFactory) {
                vc.emit('editChargeMachineFactory', 'openEditChargeMachineFactoryModal', _chargeMachineFactory);
            },
            _openDeleteChargeMachineFactoryModel: function (_chargeMachineFactory) {
                vc.emit('deleteChargeMachineFactory', 'openDeleteChargeMachineFactoryModal', _chargeMachineFactory);
            },
            _openChargeMachineFactorySpec: function (_chargeMachineFactory) {
                vc.jumpToPage('/#/pages/charge/chargeMachineFactorySpecManage?factoryId=' + _chargeMachineFactory.factoryId);
            },
            _queryChargeMachineFactoryMethod: function () {
                $that._listChargeMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetChargeMachineFactoryMethod: function () {
                $that.chargeMachineFactoryManageInfo.conditions = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                }
                $that._listChargeMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);
