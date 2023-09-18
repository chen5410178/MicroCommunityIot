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
            vc.component._listLiftMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftMachineFactorySpecManage', 'listLiftMachineFactorySpec', function (_param) {
                vc.component._listLiftMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listLiftMachineFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineFactorySpecs: function (_page, _rows) {

                vc.component.liftMachineFactorySpecManageInfo.conditions.page = _page;
                vc.component.liftMachineFactorySpecManageInfo.conditions.row = _rows;
                var param = {
                    params: vc.component.liftMachineFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('liftMachineFactorySpec.listLiftMachineFactorySpec',
                    param,
                    function (json, res) {
                        var _liftMachineFactorySpecManageInfo = JSON.parse(json);
                        vc.component.liftMachineFactorySpecManageInfo.total = _liftMachineFactorySpecManageInfo.total;
                        vc.component.liftMachineFactorySpecManageInfo.records = _liftMachineFactorySpecManageInfo.records;
                        vc.component.liftMachineFactorySpecManageInfo.liftMachineFactorySpecs = _liftMachineFactorySpecManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.liftMachineFactorySpecManageInfo.records,
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
                vc.component._listLiftMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if (vc.component.liftMachineFactorySpecManageInfo.moreCondition) {
                    vc.component.liftMachineFactorySpecManageInfo.moreCondition = false;
                } else {
                    vc.component.liftMachineFactorySpecManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
