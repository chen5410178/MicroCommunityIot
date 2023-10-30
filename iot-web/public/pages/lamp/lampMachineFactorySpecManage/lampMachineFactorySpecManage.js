/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lampMachineFactorySpecManageInfo: {
                lampMachineFactorySpecs: [],
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
            $that.lampMachineFactorySpecManageInfo.conditions.factoryId = vc.getParam('factoryId');
            $that._listLampMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('lampMachineFactorySpecManage', 'listLampMachineFactorySpec', function (_param) {
                $that._listLampMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLampMachineFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLampMachineFactorySpecs: function (_page, _rows) {

                $that.lampMachineFactorySpecManageInfo.conditions.page = _page;
                $that.lampMachineFactorySpecManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lampMachineFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lampMachineFactorySpec.listLampMachineFactorySpec',
                    param,
                    function (json, res) {
                        var _lampMachineFactorySpecManageInfo = JSON.parse(json);
                        $that.lampMachineFactorySpecManageInfo.total = _lampMachineFactorySpecManageInfo.total;
                        $that.lampMachineFactorySpecManageInfo.records = _lampMachineFactorySpecManageInfo.records;
                        $that.lampMachineFactorySpecManageInfo.lampMachineFactorySpecs = _lampMachineFactorySpecManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lampMachineFactorySpecManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLampMachineFactorySpecModal: function () {
                vc.emit('addLampMachineFactorySpec', 'openAddLampMachineFactorySpecModal', $that.lampMachineFactorySpecManageInfo.conditions.factoryId);
            },
            _openDeleteLampMachineFactorySpecModel: function (_lampMachineFactorySpec) {
                vc.emit('deleteLampMachineFactorySpec', 'openDeleteLampMachineFactorySpecModal', _lampMachineFactorySpec);
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
