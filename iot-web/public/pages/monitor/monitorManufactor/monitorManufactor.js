/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            monitorManufactorInfo: {
                monitorManufactors: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mmId: '',
                conditions: {
                    mmName: '',
                    mmUrl: '',
                    appId: '',
                    appSecure: '',
                }
            }
        },
        _initMethod: function () {
            $that._listMonitorManufactors(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('monitorManufactor', 'listMonitorManufactor', function (_param) {
                $that._listMonitorManufactors(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMonitorManufactors(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMonitorManufactors: function (_page, _rows) {

                $that.monitorManufactorInfo.conditions.page = _page;
                $that.monitorManufactorInfo.conditions.row = _rows;
                $that.monitorManufactorInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.monitorManufactorInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/monitorManufactor.listMonitorManufactor',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorManufactorInfo.total = _json.total;
                        $that.monitorManufactorInfo.records = _json.records;
                        $that.monitorManufactorInfo.monitorManufactors = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.monitorManufactorInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMonitorManufactorModal: function () {
                vc.emit('addMonitorManufactor', 'openAddMonitorManufactorModal', {});
            },
            _openEditMonitorManufactorModel: function (_monitorManufactor) {
                vc.emit('editMonitorManufactor', 'openEditMonitorManufactorModal', _monitorManufactor);
            },
            _openDeleteMonitorManufactorModel: function (_monitorManufactor) {
                vc.emit('deleteMonitorManufactor', 'openDeleteMonitorManufactorModal', _monitorManufactor);
            },
            _queryMonitorManufactorMethod: function () {
                $that._listMonitorManufactors(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.monitorManufactorInfo.moreCondition) {
                    $that.monitorManufactorInfo.moreCondition = false;
                } else {
                    $that.monitorManufactorInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
