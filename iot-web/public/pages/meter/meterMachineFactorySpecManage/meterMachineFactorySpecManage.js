/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterMachineFactorySpecManageInfo: {
                meterMachineFactorySpecs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                specId: '',
                conditions: {
                    specId: '',
                    factoryId: '',
                    specName: '',
                }
            }
        },
        _initMethod: function () {
            $that.meterMachineFactorySpecManageInfo.conditions.factoryId = vc.getParam("factoryId");
            $that._listMeterMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('meterMachineFactorySpecManage', 'listMeterMachineFactorySpec', function (_param) {
                $that._listMeterMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMeterMachineFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterMachineFactorySpecs: function (_page, _rows) {

                $that.meterMachineFactorySpecManageInfo.conditions.page = _page;
                $that.meterMachineFactorySpecManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.meterMachineFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/meterMachineFactorySpec.listMeterMachineFactorySpec',
                    param,
                    function (json, res) {
                        var _meterMachineFactorySpecManageInfo = JSON.parse(json);
                        $that.meterMachineFactorySpecManageInfo.total = _meterMachineFactorySpecManageInfo.total;
                        $that.meterMachineFactorySpecManageInfo.records = _meterMachineFactorySpecManageInfo.records;
                        $that.meterMachineFactorySpecManageInfo.meterMachineFactorySpecs = _meterMachineFactorySpecManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.meterMachineFactorySpecManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMeterMachineFactorySpecModal: function () {
                vc.emit('addMeterMachineFactorySpec', 'openAddMeterMachineFactorySpecModal', $that.meterMachineFactorySpecManageInfo.conditions.factoryId);
            },
            _openEditMeterMachineFactorySpecModel: function (_meterMachineFactorySpec) {
                vc.emit('editMeterMachineFactorySpec', 'openEditMeterMachineFactorySpecModal', _meterMachineFactorySpec);
            },
            _openDeleteMeterMachineFactorySpecModel: function (_meterMachineFactorySpec) {
                vc.emit('deleteMeterMachineFactorySpec', 'openDeleteMeterMachineFactorySpecModal', _meterMachineFactorySpec);
            },
            _queryMeterMachineFactorySpecMethod: function () {
                $that._listMeterMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);
