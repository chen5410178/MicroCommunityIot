/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            instrumentFactorySpecManageInfo: {
                instrumentFactorySpecs: [],
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
            if (!vc.getParam("factoryId")) {
                return;
            }
            $that.instrumentFactorySpecManageInfo.conditions.factoryId = vc.getParam("factoryId");
            $that._listInstrumentFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('instrumentFactorySpecManage', 'listInstrumentFactorySpec', function (_param) {
                $that._listInstrumentFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listInstrumentFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listInstrumentFactorySpecs: function (_page, _rows) {

                $that.instrumentFactorySpecManageInfo.conditions.page = _page;
                $that.instrumentFactorySpecManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.instrumentFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/instrumentFactorySpec.listInstrumentFactorySpec',
                    param,
                    function (json, res) {
                        var _instrumentFactorySpecManageInfo = JSON.parse(json);
                        $that.instrumentFactorySpecManageInfo.total = _instrumentFactorySpecManageInfo.total;
                        $that.instrumentFactorySpecManageInfo.records = _instrumentFactorySpecManageInfo.records;
                        $that.instrumentFactorySpecManageInfo.instrumentFactorySpecs = _instrumentFactorySpecManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.instrumentFactorySpecManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddInstrumentFactorySpecModal: function () {
                vc.emit('addInstrumentFactorySpec', 'openAddInstrumentFactorySpecModal', $that.instrumentFactorySpecManageInfo.conditions.factoryId);
            },
            _openDeleteInstrumentFactorySpecModel: function (_instrumentFactorySpec) {
                vc.emit('deleteInstrumentFactorySpec', 'openDeleteInstrumentFactorySpecModal', _instrumentFactorySpec);
            },
            _queryInstrumentFactorySpecMethod: function () {
                $that._listInstrumentFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
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
