/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            telCallLogInfo: {
                telCallLogs: [],
                machines:[],
                total: 0,
                records: 1,
                moreCondition: false,
                tmuId: '',
                conditions: {
                    machineId: '',
                    staffId: '',
                    staffName: '',
                }
            }
        },
        _initMethod: function () {
            $that._listMachines();
        },
        _initEvent: function () {
            vc.on('telCallLog', 'listTelCallLog', function (_param) {
                $that._listTelCallLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listTelCallLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTelCallLogs: function (_page, _rows) {

                $that.telCallLogInfo.conditions.page = _page;
                $that.telCallLogInfo.conditions.row = _rows;
                $that.telCallLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.telCallLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/telCallLog.listTelCallLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telCallLogInfo.total = _json.total;
                        $that.telCallLogInfo.records = _json.records;
                        $that.telCallLogInfo.telCallLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.telCallLogInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
           
            _queryTelCallLogMethod: function () {
                $that._listTelCallLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.telCallLogInfo.moreCondition) {
                    $that.telCallLogInfo.moreCondition = false;
                } else {
                    $that.telCallLogInfo.moreCondition = true;
                }
            },
            _swatchMachine:function(_machine){
                $that.telCallLogInfo.conditions.machineId = _machine.machineId;
                $that._listTelCallLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listMachines: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/telMachine.listTelMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telCallLogInfo.machines = _json.data;
                        if (_json.data && _json.data.length > 0) {
                            $that._swatchMachine(_json.data[0]);
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

        }
    });
})(window.vc);
