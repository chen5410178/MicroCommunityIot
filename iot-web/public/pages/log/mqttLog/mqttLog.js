/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            mqttLogInfo: {
                mqttLogs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                mqttId: '',
                conditions: {
                    startTime: '',
                    endTime: '',
                }
            }
        },
        _initMethod: function() {
            vc.initDate('startDate', function(_value) {
                $that.mqttLogInfo.conditions.startTime = _value;
            });
            vc.initDate('endDate', function(_value) {
                $that.mqttLogInfo.conditions.endTime = _value;
            })
            $that._listMqttLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('mqttLog', 'listMqttLog', function(_param) {
                $that._listMqttLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listMqttLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMqttLogs: function(_page, _rows) {

                $that.mqttLogInfo.conditions.page = _page;
                $that.mqttLogInfo.conditions.row = _rows;
                let param = {
                    params: $that.mqttLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/log.listMqttLog',
                    param,
                    function(json, res) {
                        let _mqttLogInfo = JSON.parse(json);
                        $that.mqttLogInfo.total = _mqttLogInfo.total;
                        $that.mqttLogInfo.records = _mqttLogInfo.records;
                        $that.mqttLogInfo.mqttLogs = _mqttLogInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.mqttLogInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _queryMqttLogMethod: function() {
                $that._listMqttLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function() {
                if ($that.mqttLogInfo.moreCondition) {
                    $that.mqttLogInfo.moreCondition = false;
                } else {
                    $that.mqttLogInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);