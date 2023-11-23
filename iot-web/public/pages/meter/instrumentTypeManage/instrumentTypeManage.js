/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            instrumentTypeManageInfo: {
                instrumentTypes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                typeId: '',
                conditions: {
                    typeId: '',
                    name: '',
                    communityId: '',
                }
            }
        },
        _initMethod: function () {
            $that._listInstrumentTypes(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('instrumentTypeManage', 'listInstrumentType', function (_param) {
                $that._listInstrumentTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listInstrumentTypes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listInstrumentTypes: function (_page, _rows) {

                $that.instrumentTypeManageInfo.conditions.page = _page;
                $that.instrumentTypeManageInfo.conditions.row = _rows;
                $that.instrumentTypeManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.instrumentTypeManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/instrumentType.listInstrumentType',
                    param,
                    function (json, res) {
                        var _instrumentTypeManageInfo = JSON.parse(json);
                        $that.instrumentTypeManageInfo.total = _instrumentTypeManageInfo.total;
                        $that.instrumentTypeManageInfo.records = _instrumentTypeManageInfo.records;
                        $that.instrumentTypeManageInfo.instrumentTypes = _instrumentTypeManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.instrumentTypeManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddInstrumentTypeModal: function () {
                vc.emit('addInstrumentType', 'openAddInstrumentTypeModal', {});
            },
            _openEditInstrumentTypeModel: function (_instrumentType) {
                vc.emit('editInstrumentType', 'openEditInstrumentTypeModal', _instrumentType);
            },
            _openDeleteInstrumentTypeModel: function (_instrumentType) {
                vc.emit('deleteInstrumentType', 'openDeleteInstrumentTypeModal', _instrumentType);
            },
            _queryInstrumentTypeMethod: function () {
                $that._listInstrumentTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetInstrumentTypeMethod: function () {
                $that.instrumentTypeManageInfo.conditions = {
                    typeId: '',
                    name: '',
                    communityId: '',
                }
                $that._listInstrumentTypes(DEFAULT_PAGE, DEFAULT_ROWS);
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
