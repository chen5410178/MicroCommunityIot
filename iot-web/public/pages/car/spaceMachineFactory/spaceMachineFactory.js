/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            spaceMachineFactoryInfo: {
                spaceMachineFactorys: [],
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
            $that._listSpaceMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('spaceMachineFactory', 'listSpaceMachineFactory', function (_param) {
                $that._listSpaceMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listSpaceMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listSpaceMachineFactorys: function (_page, _rows) {

                $that.spaceMachineFactoryInfo.conditions.page = _page;
                $that.spaceMachineFactoryInfo.conditions.row = _rows;
                let param = {
                    params: $that.spaceMachineFactoryInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/spaceMachineFactory.listSpaceMachineFactory',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.spaceMachineFactoryInfo.total = _json.total;
                        $that.spaceMachineFactoryInfo.records = _json.records;
                        $that.spaceMachineFactoryInfo.spaceMachineFactorys = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.spaceMachineFactoryInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddSpaceMachineFactoryModal: function () {
                vc.emit('addSpaceMachineFactory', 'openAddSpaceMachineFactoryModal', {});
            },
            _openEditSpaceMachineFactoryModel: function (_spaceMachineFactory) {
                vc.emit('editSpaceMachineFactory', 'openEditSpaceMachineFactoryModal', _spaceMachineFactory);
            },
            _openDeleteSpaceMachineFactoryModel: function (_spaceMachineFactory) {
                vc.emit('deleteSpaceMachineFactory', 'openDeleteSpaceMachineFactoryModal', _spaceMachineFactory);
            },
            _querySpaceMachineFactoryMethod: function () {
                $that._listSpaceMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.spaceMachineFactoryInfo.moreCondition) {
                    $that.spaceMachineFactoryInfo.moreCondition = false;
                } else {
                    $that.spaceMachineFactoryInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
