/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lampMachineFactoryManageInfo: {
                lampMachineFactorys: [],
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
            $that._listLampMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('lampMachineFactoryManage', 'listLampMachineFactory', function (_param) {
                $that._listLampMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLampMachineFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLampMachineFactorys: function (_page, _rows) {

                $that.lampMachineFactoryManageInfo.conditions.page = _page;
                $that.lampMachineFactoryManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lampMachineFactoryManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lampMachineFactory.listLampMachineFactory',
                    param,
                    function (json, res) {
                        var _lampMachineFactoryManageInfo = JSON.parse(json);
                        $that.lampMachineFactoryManageInfo.total = _lampMachineFactoryManageInfo.total;
                        $that.lampMachineFactoryManageInfo.records = _lampMachineFactoryManageInfo.records;
                        $that.lampMachineFactoryManageInfo.lampMachineFactorys = _lampMachineFactoryManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lampMachineFactoryManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLampMachineFactoryModal: function () {
                vc.emit('addLampMachineFactory', 'openAddLampMachineFactoryModal', {});
            },
            _openEditLampMachineFactoryModel: function (_lampMachineFactory) {
                vc.emit('editLampMachineFactory', 'openEditLampMachineFactoryModal', _lampMachineFactory);
            },
            _openDeleteLampMachineFactoryModel: function (_lampMachineFactory) {
                vc.emit('deleteLampMachineFactory', 'openDeleteLampMachineFactoryModal', _lampMachineFactory);
            },
            _openLampMachineFactorySpec: function (_lampMachineFactory) {
                vc.jumpToPage("/#/pages/lamp/lampMachineFactorySpecManage?factoryId=" + _lampMachineFactory.factoryId);
            },
            _queryLampMachineFactoryMethod: function () {
                $that._listLampMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLampMachineFactoryMethod: function () {
                $that.lampMachineFactoryManageInfo.conditions = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                };
                $that._listLampMachineFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
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
