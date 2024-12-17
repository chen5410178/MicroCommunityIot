/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            intercomLogInfo: {
                intercomLogs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    amachineCode: '',
                    amachineName: '',
                    state: '',
                    communityId: '',

                }
            }
        },
        _initMethod: function () {
            $that._listintercomLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('intercomLog', 'listintercomLog', function (_param) {
                $that._listintercomLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listintercomLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listintercomLogs: function (_page, _rows) {

                $that.intercomLogInfo.conditions.page = _page;
                $that.intercomLogInfo.conditions.row = _rows;
                $that.intercomLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.intercomLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/ai.getIntercomLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.intercomLogInfo.total = _json.total;
                        $that.intercomLogInfo.records = _json.records;
                        $that.intercomLogInfo.intercomLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.intercomLogInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryintercomLogMethod: function () {
                $that._listintercomLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.intercomLogInfo.moreCondition) {
                    $that.intercomLogInfo.moreCondition = false;
                } else {
                    $that.intercomLogInfo.moreCondition = true;
                }
            },
            _getLogStateName:function(_type){//W 呼叫中，D 通话中,F呼叫失败，C通话完成
                if(_type == 'W'){
                    return "呼叫中";
                }
                if(_type == 'D'){
                    return "通话中";
                }
                if(_type == 'F'){
                    return "呼叫失败";
                }
                if(_type == 'C'){
                    return "通话完成";
                }
            }


        }
    });
})(window.vc);
