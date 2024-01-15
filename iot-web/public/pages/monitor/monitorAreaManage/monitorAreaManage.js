/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorAreaManageInfo: {
                monitorAreas: [],
                total: 0,
                records: 1,
                moreCondition: false,
                maId: '',
                conditions: {
                    maId: '',
                    maName: '',
                    communityId: vc.getCurrentCommunity().communityId,
                }
            }
        },
        _initMethod: function () {
            $that._listMonitorAreas(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('monitorAreaManage', 'listMonitorArea', function (_param) {
                $that._listMonitorAreas(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorAreas(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorAreas: function (_page, _rows) {
                $that.monitorAreaManageInfo.conditions.page = _page;
                $that.monitorAreaManageInfo.conditions.row = _rows;
                let param = {
                    params: $that.monitorAreaManageInfo.conditions
                };
                //发送get请求
                vc.http.apiGet('/monitorArea.listMonitorArea',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorAreaManageInfo.total = _json.total;
                        $that.monitorAreaManageInfo.records = _json.records;
                        $that.monitorAreaManageInfo.monitorAreas = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorAreaManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMonitorAreaModal: function () {
                vc.emit('addMonitorArea', 'openAddMonitorAreaModal', {});
            },
            _openEditMonitorAreaModel: function (_monitorArea) {
                vc.emit('editMonitorArea', 'openEditMonitorAreaModal', _monitorArea);
            },
            _openDeleteMonitorAreaModel: function (_monitorArea) {
                vc.emit('deleteMonitorArea', 'openDeleteMonitorAreaModal', _monitorArea);
            },
            _queryMonitorAreaMethod: function () {
                $that._listMonitorAreas(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMonitorAreaMethod: function () {
                $that.monitorAreaManageInfo.conditions = {
                    maId: '',
                    maName: '',
                    communityId: vc.getCurrentCommunity().communityId,
                };
                $that._listMonitorAreas(DEFAULT_PAGE, DEFAULT_ROWS);
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