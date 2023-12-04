/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeMachineFactorySpecManageInfo: {
                chargeMachineFactorySpecs: [],
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
            $that.chargeMachineFactorySpecManageInfo.conditions.factoryId = vc.getParam("factoryId");
            $that._listChargeMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('chargeMachineFactorySpecManage', 'listChargeMachineFactorySpec', function (_param) {
                $that._listChargeMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listChargeMachineFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachineFactorySpecs: function (_page, _rows) {

                $that.chargeMachineFactorySpecManageInfo.conditions.page = _page;
                $that.chargeMachineFactorySpecManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.chargeMachineFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachineFactorySpec.listChargeMachineFactorySpec',
                    param,
                    function (json, res) {
                        var _chargeMachineFactorySpecManageInfo = JSON.parse(json);
                        $that.chargeMachineFactorySpecManageInfo.total = _chargeMachineFactorySpecManageInfo.total;
                        $that.chargeMachineFactorySpecManageInfo.records = _chargeMachineFactorySpecManageInfo.records;
                        $that.chargeMachineFactorySpecManageInfo.chargeMachineFactorySpecs = _chargeMachineFactorySpecManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeMachineFactorySpecManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeMachineFactorySpecModal: function () {
                vc.emit('addChargeMachineFactorySpec', 'openAddChargeMachineFactorySpecModal', $that.chargeMachineFactorySpecManageInfo.conditions.factoryId);
            },
            _openDeleteChargeMachineFactorySpecModel: function (_chargeMachineFactorySpec) {
                vc.emit('deleteChargeMachineFactorySpec', 'openDeleteChargeMachineFactorySpecModal', _chargeMachineFactorySpec);
            },
            _queryChargeMachineFactorySpecMethod: function () {
                $that._listChargeMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetChargeMachineFactorySpecMethod: function () {
                $that.chargeMachineFactorySpecManageInfo.conditions = {
                    specId: '',
                    factoryId: '',
                    specName: '',
                }
                $that._listChargeMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);
