/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterMachineFactoryManageInfo: {
                meterMachineFactorys: [],
                total: 0,
                records: 1,
                moreCondition: false,
                factoryId: '',
                conditions: {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    machineModel: '',
                }
            }
        },
        _initMethod: function () {
            $that._listMeterMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('meterMachineFactoryManage', 'listMeterMachineFactory', function (_param) {
                $that._listMeterMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMeterMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterMachineFactorys: function (_page, _rows) {
                $that.meterMachineFactoryManageInfo.conditions.page = _page;
                $that.meterMachineFactoryManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.meterMachineFactoryManageInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachineFactory',
                    param,
                    function (json, res) {
                        var _meterMachineFactoryManageInfo = JSON.parse(json);
                        $that.meterMachineFactoryManageInfo.total = _meterMachineFactoryManageInfo.total;
                        $that.meterMachineFactoryManageInfo.records = _meterMachineFactoryManageInfo.records;
                        $that.meterMachineFactoryManageInfo.meterMachineFactorys = _meterMachineFactoryManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.meterMachineFactoryManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMeterMachineFactoryModal: function () {
                vc.emit('addMeterMachineFactory', 'openAddMeterMachineFactoryModal', {});
            },
            _openEditMeterMachineFactoryModel: function (_meterMachineFactory) {
                vc.emit('editMeterMachineFactory', 'openEditMeterMachineFactoryModal', _meterMachineFactory);
            },
            _openDeleteMeterMachineFactoryModel: function (_meterMachineFactory) {
                vc.emit('deleteMeterMachineFactory', 'openDeleteMeterMachineFactoryModal', _meterMachineFactory);
            },
            _openMeterMachineFactorySpec: function (_meterMachineFactory) {
                vc.jumpToPage("/#/pages/meter/meterMachineFactorySpecManage?factoryId=" + _meterMachineFactory.factoryId);
            },
            _queryMeterMachineFactoryMethod: function () {
                $that._listMeterMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMeterMachineFactoryMethod: function () {
                $that.meterMachineFactoryManageInfo.conditions = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                    machineModel: '',
                }
                $that._listMeterMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.meterMachineFactoryManageInfo.moreCondition) {
                    $that.meterMachineFactoryManageInfo.moreCondition = false;
                } else {
                    $that.meterMachineFactoryManageInfo.moreCondition = true;
                }
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
