/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineFactorySpecManageInfo: {
                liftMachineFactorySpecs: [],
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
            $that.liftMachineFactorySpecManageInfo.conditions.factoryId = vc.getParam('factoryId');
            $that._listLiftMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftMachineFactorySpecManage', 'listLiftMachineFactorySpec', function (_param) {
                $that._listLiftMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftMachineFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineFactorySpecs: function (_page, _rows) {

                $that.liftMachineFactorySpecManageInfo.conditions.page = _page;
                $that.liftMachineFactorySpecManageInfo.conditions.row = _rows;
                let param = {
                    params: $that.liftMachineFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('liftMachineFactorySpec.listLiftMachineFactorySpec',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineFactorySpecManageInfo.total = _json.total;
                        $that.liftMachineFactorySpecManageInfo.records = _json.records;
                        $that.liftMachineFactorySpecManageInfo.liftMachineFactorySpecs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftMachineFactorySpecManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLiftMachineFactorySpecModal: function () {
                vc.emit('addLiftMachineFactorySpec', 'openAddLiftMachineFactorySpecModal', $that.liftMachineFactorySpecManageInfo.conditions.factoryId);
            },
            _openEditLiftMachineFactorySpecModel: function (_liftMachineFactorySpec) {
                vc.emit('editLiftMachineFactorySpec', 'openEditLiftMachineFactorySpecModal', _liftMachineFactorySpec);
            },
            _openDeleteLiftMachineFactorySpecModel: function (_liftMachineFactorySpec) {
                vc.emit('deleteLiftMachineFactorySpec', 'openDeleteLiftMachineFactorySpecModal', _liftMachineFactorySpec);
            },
            _queryLiftMachineFactorySpecMethod: function () {
                $that._listLiftMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
        }
    });
})(window.vc);
