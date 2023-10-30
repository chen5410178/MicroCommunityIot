/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lockMachineFactorySpecManageInfo: {
                lockMachineFactorySpecs: [],
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
            $that.lockMachineFactorySpecManageInfo.conditions.factoryId = vc.getParam('factoryId');
            $that._listLockMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('lockMachineFactorySpecManage', 'listLockMachineFactorySpec', function (_param) {
                $that._listLockMachineFactorySpecs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLockMachineFactorySpecs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLockMachineFactorySpecs: function (_page, _rows) {

                $that.lockMachineFactorySpecManageInfo.conditions.page = _page;
                $that.lockMachineFactorySpecManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.lockMachineFactorySpecManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lockMachineFactorySpec.listLockMachineFactorySpec',
                    param,
                    function (json, res) {
                        var _lockMachineFactorySpecManageInfo = JSON.parse(json);
                        $that.lockMachineFactorySpecManageInfo.total = _lockMachineFactorySpecManageInfo.total;
                        $that.lockMachineFactorySpecManageInfo.records = _lockMachineFactorySpecManageInfo.records;
                        $that.lockMachineFactorySpecManageInfo.lockMachineFactorySpecs = _lockMachineFactorySpecManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lockMachineFactorySpecManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddLockMachineFactorySpecModal: function () {
                vc.emit('addLockMachineFactorySpec', 'openAddLockMachineFactorySpecModal', $that.lockMachineFactorySpecManageInfo.conditions.factoryId);
            },
            _openDeleteLockMachineFactorySpecModel: function (_lockMachineFactorySpec) {
                vc.emit('deleteLockMachineFactorySpec', 'openDeleteLockMachineFactorySpecModal', _lockMachineFactorySpec);
            },
        }
    });
})(window.vc);
