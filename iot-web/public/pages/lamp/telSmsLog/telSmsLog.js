/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            telSmsLogInfo: {
                telSmsLogs: [],
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
            vc.on('telSmsLog', 'listTelSmsLog', function (_param) {
                $that._listTelSmsLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listTelSmsLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTelSmsLogs: function (_page, _rows) {

                $that.telSmsLogInfo.conditions.page = _page;
                $that.telSmsLogInfo.conditions.row = _rows;
                $that.telSmsLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.telSmsLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/telSmsLog.listTelSmsLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telSmsLogInfo.total = _json.total;
                        $that.telSmsLogInfo.records = _json.records;
                        $that.telSmsLogInfo.telSmsLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.telSmsLogInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
           
            _queryTelSmsLogMethod: function () {
                $that._listTelSmsLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.telSmsLogInfo.moreCondition) {
                    $that.telSmsLogInfo.moreCondition = false;
                } else {
                    $that.telSmsLogInfo.moreCondition = true;
                }
            },
            _swatchMachine:function(_machine){
                $that.telSmsLogInfo.conditions.machineId = _machine.machineId;
                $that._listTelSmsLogs(DEFAULT_PAGE, DEFAULT_ROWS);
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
                        $that.telSmsLogInfo.machines = _json.data;
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
