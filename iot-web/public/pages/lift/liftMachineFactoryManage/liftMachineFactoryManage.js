/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineFactoryManageInfo: {
                liftMachineFactorys: [],
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
            vc.component._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftMachineFactoryManage', 'listLiftMachineFactory', function (_param) {
                vc.component._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listLiftMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineFactorys: function (_page, _rows) {

                vc.component.liftMachineFactoryManageInfo.conditions.page = _page;
                vc.component.liftMachineFactoryManageInfo.conditions.row = _rows;
                var param = {
                    params: vc.component.liftMachineFactoryManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('liftMachineFactory.listLiftMachineFactory',
                    param,
                    function (json, res) {
                        var _liftMachineFactoryManageInfo = JSON.parse(json);
                        vc.component.liftMachineFactoryManageInfo.total = _liftMachineFactoryManageInfo.total;
                        vc.component.liftMachineFactoryManageInfo.records = _liftMachineFactoryManageInfo.records;
                        vc.component.liftMachineFactoryManageInfo.liftMachineFactorys = _liftMachineFactoryManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.liftMachineFactoryManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLiftMachineFactoryModal: function () {
                vc.emit('addLiftMachineFactory', 'openAddLiftMachineFactoryModal', {});
            },
            _openEditLiftMachineFactoryModel: function (_liftMachineFactory) {
                vc.emit('editLiftMachineFactory', 'openEditLiftMachineFactoryModal', _liftMachineFactory);
            },
            _openDeleteLiftMachineFactoryModel: function (_liftMachineFactory) {
                vc.emit('deleteLiftMachineFactory', 'openDeleteLiftMachineFactoryModal', _liftMachineFactory);
            },
            _openLiftMachineFactorySpec: function (_liftMachineFactory) {
                vc.jumpToPage('/#/pages/lift/liftMachineFactorySpecManage?factoryId=' + _liftMachineFactory.factoryId);
            },
            _queryLiftMachineFactoryMethod: function () {
                vc.component._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _resetLiftMachineFactoryMethod: function () {
                $that.liftMachineFactoryManageInfo.conditions.factoryName = '';
                $that.liftMachineFactoryManageInfo.conditions.factoryId = '';
                $that.liftMachineFactoryManageInfo.conditions.beanImpl = '';
                $that._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            }
        }
    });
})(window.vc);
