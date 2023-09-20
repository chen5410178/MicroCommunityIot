/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineLogManageInfo: {
                liftMachineLogs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    machineId: '',
                    communityId: '',
                    logAction: '',
                    state: '',
                    createTime: '',
                }
            }
        },
        _initMethod: function () {
            vc.component._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftMachineLogManage', 'listLiftMachineLog', function (_param) {
                vc.component._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listLiftMachineLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineLogs: function (_page, _rows) {
                vc.component.liftMachineLogManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                vc.component.liftMachineLogManageInfo.conditions.page = _page;
                vc.component.liftMachineLogManageInfo.conditions.row = _rows;
                var param = {
                    params: vc.component.liftMachineLogManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('liftMachineLog.listLiftMachineLog',
                    param,
                    function (json, res) {
                        var _liftMachineLogManageInfo = JSON.parse(json);
                        vc.component.liftMachineLogManageInfo.total = _liftMachineLogManageInfo.total;
                        vc.component.liftMachineLogManageInfo.records = _liftMachineLogManageInfo.records;
                        vc.component.liftMachineLogManageInfo.liftMachineLogs = _liftMachineLogManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.liftMachineLogManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            /*_openAddLiftMachineLogModal: function () {
                vc.emit('addLiftMachineLog', 'openAddLiftMachineLogModal', {});
            },
            _openEditLiftMachineLogModel: function (_liftMachineLog) {
                vc.emit('editLiftMachineLog', 'openEditLiftMachineLogModal', _liftMachineLog);
            },
            _openDeleteLiftMachineLogModel: function (_liftMachineLog) {
                vc.emit('deleteLiftMachineLog', 'openDeleteLiftMachineLogModal', _liftMachineLog);
            },*/
            _queryLiftMachineLogMethod: function () {
                vc.component._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _resetLiftMachineLogMethod: function () {
                $that.liftMachineLogManageInfo.conditions.machineId = '';
                $that.liftMachineLogManageInfo.conditions.communityId = '';
                $that.liftMachineLogManageInfo.conditions.logAction = '';
                $that.liftMachineLogManageInfo.conditions.createTime = '';
                $that.liftMachineLogManageInfo.conditions.state = '';
                $that._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if (vc.component.liftMachineLogManageInfo.moreCondition) {
                    vc.component.liftMachineLogManageInfo.moreCondition = false;
                } else {
                    vc.component.liftMachineLogManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
