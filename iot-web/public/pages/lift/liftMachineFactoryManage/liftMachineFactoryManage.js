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
            $that._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftMachineFactoryManage', 'listLiftMachineFactory', function (_param) {
                $that._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineFactorys: function (_page, _rows) {

                $that.liftMachineFactoryManageInfo.conditions.page = _page;
                $that.liftMachineFactoryManageInfo.conditions.row = _rows;
                let param = {
                    params: $that.liftMachineFactoryManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftMachineFactory.listLiftMachineFactory',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineFactoryManageInfo.total = _json.total;
                        $that.liftMachineFactoryManageInfo.records = _json.records;
                        $that.liftMachineFactoryManageInfo.liftMachineFactorys = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftMachineFactoryManageInfo.records,
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
                $that._listLiftMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);

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
