/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lockMachineFactoryManageInfo: {
                lockMachineFactorys: [],
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
            $that._listLockMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('lockMachineFactoryManage', 'listLockMachineFactory', function (_param) {
                $that._listLockMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLockMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLockMachineFactorys: function (_page, _rows) {
                $that.lockMachineFactoryManageInfo.conditions.page = _page;
                $that.lockMachineFactoryManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lockMachineFactoryManageInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/lockMachineFactory.listLockMachineFactory',
                    param,
                    function (json, res) {
                        var _lockMachineFactoryManageInfo = JSON.parse(json);
                        $that.lockMachineFactoryManageInfo.total = _lockMachineFactoryManageInfo.total;
                        $that.lockMachineFactoryManageInfo.records = _lockMachineFactoryManageInfo.records;
                        $that.lockMachineFactoryManageInfo.lockMachineFactorys = _lockMachineFactoryManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lockMachineFactoryManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLockMachineFactoryModal: function () {
                vc.emit('addLockMachineFactory', 'openAddLockMachineFactoryModal', {});
            },
            _openEditLockMachineFactoryModel: function (_lockMachineFactory) {
                vc.emit('editLockMachineFactory', 'openEditLockMachineFactoryModal', _lockMachineFactory);
            },
            _openDeleteLockMachineFactoryModel: function (_lockMachineFactory) {
                vc.emit('deleteLockMachineFactory', 'openDeleteLockMachineFactoryModal', _lockMachineFactory);
            },
            _openLockMachineFactorySpec: function (_lockMachineFactory) {
                vc.jumpToPage('/#/pages/lock/lockMachineFactorySpecManage?factoryId=' + _lockMachineFactory.factoryId);
            },
            _queryLockMachineFactoryMethod: function () {
                $that._listLockMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLockMachineFactoryMethod: function () {
                $that.lockMachineFactoryManageInfo.conditions = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                };
                $that._listLockMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openModel: function (_data, _title) {
                vc.emit('viewData', 'openEventViewDataModal', {
                    title: _title,
                    data: _data
                });
            },
        }
    });
})(window.vc);
