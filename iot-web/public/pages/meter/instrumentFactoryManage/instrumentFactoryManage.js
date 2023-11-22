/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            instrumentFactoryManageInfo: {
                instrumentFactorys: [],
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
            $that._listInstrumentFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('instrumentFactoryManage', 'listInstrumentFactory', function (_param) {
                $that._listInstrumentFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listInstrumentFactorys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listInstrumentFactorys: function (_page, _rows) {

                $that.instrumentFactoryManageInfo.conditions.page = _page;
                $that.instrumentFactoryManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.instrumentFactoryManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/instrumentFactory.listInstrumentFactory',
                    param,
                    function (json, res) {
                        var _instrumentFactoryManageInfo = JSON.parse(json);
                        $that.instrumentFactoryManageInfo.total = _instrumentFactoryManageInfo.total;
                        $that.instrumentFactoryManageInfo.records = _instrumentFactoryManageInfo.records;
                        $that.instrumentFactoryManageInfo.instrumentFactorys = _instrumentFactoryManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.instrumentFactoryManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddInstrumentFactoryModal: function () {
                vc.emit('addInstrumentFactory', 'openAddInstrumentFactoryModal', {});
            },
            _openEditInstrumentFactoryModel: function (_instrumentFactory) {
                vc.emit('editInstrumentFactory', 'openEditInstrumentFactoryModal', _instrumentFactory);
            },
            _openDeleteInstrumentFactoryModel: function (_instrumentFactory) {
                vc.emit('deleteInstrumentFactory', 'openDeleteInstrumentFactoryModal', _instrumentFactory);
            },
            _queryInstrumentFactoryMethod: function () {
                $that._listInstrumentFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openInstrumentFactorySpec: function (_instrumentFactory) {
                vc.jumpToPage("/#/pages/meter/instrumentFactorySpecManage?factoryId=" + _instrumentFactory.factoryId);
            },
            _resetInstrumentFactoryMethod: function () {
                $that.instrumentFactoryManageInfo.conditions = {
                    factoryId: '',
                    factoryName: '',
                    beanImpl: '',
                }
                $that._listInstrumentFactorys(DEFAULT_PAGE, DEFAULT_ROWS);
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
