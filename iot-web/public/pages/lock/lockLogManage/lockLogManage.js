/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            lockLogManageInfo: {
                lockLogs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    logId: '',
                    machineCode: '',
                    communityId: '',
                    logAction: '',
                    state: '',
                    userId: '',
                    userName: '',
                }
            },
            logAction: [],
        },
        _initMethod: function () {
            vc.getDict('lock_log', 'log_action', function (_data) {
                $that.logAction = _data;
            })
            $that._listLockLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLockLogs(_currentPage, DEFAULT_ROWS);
            });
            vc.on('lockLogManage', 'chooseOwner', function(_owner) {
                $that.lockLogManageInfo.conditions.userName = _owner.name;
                $that.lockLogManageInfo.conditions.userId = _owner.ownerId;
            });
        },
        methods: {
            _listLockLogs: function (_page, _rows) {
                $that.lockLogManageInfo.conditions.page = _page;
                $that.lockLogManageInfo.conditions.row = _rows;
                $that.lockLogManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.lockLogManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/lockLog.listLockLog',
                    param,
                    function (json, res) {
                        var _lockLogManageInfo = JSON.parse(json);
                        $that.lockLogManageInfo.total = _lockLogManageInfo.total;
                        $that.lockLogManageInfo.records = _lockLogManageInfo.records;
                        $that.lockLogManageInfo.lockLogs = _lockLogManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.lockLogManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLockLogMethod: function () {
                $that._listLockLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetLockLogMethod: function () {
                $that.lockLogManageInfo.conditions = {
                    logId: '',
                    machineCode: '',
                    communityId: '',
                    logAction: '',
                    state: '',
                    userId: '',
                    userName: '',
                };
                $that._listLockLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openViewLockLogModel: function (_log) {
                vc.emit('showLockLog', 'openShowLockLogModal', _log);
            },
            _openChooseOwner: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {});
            },
        }
    });
})(window.vc);
