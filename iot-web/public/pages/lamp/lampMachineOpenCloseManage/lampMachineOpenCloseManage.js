/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lampMachineOpenCloseManageInfo: {
                lampMachineOpenCloses: [],
                total: 0,
                records: 1,
                moreCondition: false,
                lmoId: '',
                conditions: {
                    lmoId: '',
                    machineId: '',
                    type: '',
                }
            }
        },
        _initMethod: function () {
            $that._listLampMachineOpenCloses(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('lampMachineOpenCloseManage', 'listLampMachineOpenClose', function (_param) {
                $that._listLampMachineOpenCloses(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLampMachineOpenCloses(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLampMachineOpenCloses: function (_page, _rows) {
                $that.lampMachineOpenCloseManageInfo.conditions.page = _page;
                $that.lampMachineOpenCloseManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lampMachineOpenCloseManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lampMachineOpenClose.listLampMachineOpenClose',
                    param,
                    function (json, res) {
                        var _lampMachineOpenCloseManageInfo = JSON.parse(json);
                        $that.lampMachineOpenCloseManageInfo.total = _lampMachineOpenCloseManageInfo.total;
                        $that.lampMachineOpenCloseManageInfo.records = _lampMachineOpenCloseManageInfo.records;
                        $that.lampMachineOpenCloseManageInfo.lampMachineOpenCloses = _lampMachineOpenCloseManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lampMachineOpenCloseManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLampMachineOpenCloseModal: function () {
                //vc.emit('addLampMachineOpenClose', 'openAddLampMachineOpenCloseModal', {});
                vc.jumpToPage('/#/pages/lamp/addLampMachineOpenClose');
            },
            _queryLampMachineOpenCloseMethod: function () {
                $that._listLampMachineOpenCloses(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.lampMachineOpenCloseManageInfo.moreCondition) {
                    $that.lampMachineOpenCloseManageInfo.moreCondition = false;
                } else {
                    $that.lampMachineOpenCloseManageInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
