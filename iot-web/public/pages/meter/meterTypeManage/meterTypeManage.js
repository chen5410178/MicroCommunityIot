/**
 入驻小区
 **/
(function (vc) {
    let DEFAULT_PAGE = 1;
    let DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterTypeManageInfo: {
                meterTypes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                typeId: '',
                conditions: {
                    typeId: '',
                    typeName: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function () {
            $that._listMeterTypes(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('meterTypeManage', 'listMeterType', function (_param) {
                $that._listMeterTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMeterTypes(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterTypes: function (_page, _rows) {
                $that.meterTypeManageInfo.conditions.page = _page;
                $that.meterTypeManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.meterTypeManageInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/meterType.listMeterType',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.meterTypeManageInfo.total = _json.total;
                        $that.meterTypeManageInfo.records = _json.records;
                        $that.meterTypeManageInfo.meterTypes = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.meterTypeManageInfo.records,
                            dataCount: $that.meterTypeManageInfo.total,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMeterTypeModal: function () {
                vc.emit('addMeterType', 'openAddMeterTypeModal', {});
            },
            _openEditMeterTypeModel: function (_meterType) {
                vc.emit('editMeterType', 'openEditMeterTypeModal', _meterType);
            },
            _openDeleteMeterTypeModel: function (_meterType) {
                vc.emit('deleteMeterType', 'openDeleteMeterTypeModal', _meterType);
            },
            _queryMeterTypeMethod: function () {
                $that._listMeterTypes(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.meterTypeManageInfo.moreCondition) {
                    $that.meterTypeManageInfo.moreCondition = false;
                } else {
                    $that.meterTypeManageInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
