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
                    machineName: '',
                    machineCode: '',
                    communityId: '',
                    logAction: '',
                    state: '',
                    createTime: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function () {
            $that._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.liftMachineLogManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.liftMachineLogManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('liftMachineLogManage', 'listLiftMachineLog', function (_param) {
                $that._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftMachineLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineLogs: function (_page, _rows) {
                $that.liftMachineLogManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                $that.liftMachineLogManageInfo.conditions.page = _page;
                $that.liftMachineLogManageInfo.conditions.row = _rows;
                let param = {
                    params: $that.liftMachineLogManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftMachineLog.listLiftMachineLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineLogManageInfo.total = _json.total;
                        $that.liftMachineLogManageInfo.records = _json.records;
                        $that.liftMachineLogManageInfo.liftMachineLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftMachineLogManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLiftMachineLogMethod: function () {
                $that._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _resetLiftMachineLogMethod: function () {
                $that.liftMachineLogManageInfo.conditions.machineId = '';
                $that.liftMachineLogManageInfo.conditions.communityId = '';
                $that.liftMachineLogManageInfo.conditions.logAction = '';
                $that.liftMachineLogManageInfo.conditions.createTime = '';
                $that.liftMachineLogManageInfo.conditions.state = '';
                $that.liftMachineLogManageInfo.conditions.queryStartTime = '';
                $that.liftMachineLogManageInfo.conditions.queryEndTime = '';
                $that._listLiftMachineLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.liftMachineLogManageInfo.moreCondition) {
                    $that.liftMachineLogManageInfo.moreCondition = false;
                } else {
                    $that.liftMachineLogManageInfo.moreCondition = true;
                }
            },
            _openViewLiftMachineLogModel: function (_log) {
                vc.emit('showLiftMachineLog', 'openShowLiftMachineLogModal', _log);
            }
        }
    });
})(window.vc);
