(function (vc) {
    vc.extends({
        data: {
            indexMonitorAlarmInfo: {
                monitorAlarms: [],
                scrollInterval: null,
                scrollTopValue: 0,
                scrollDirection: true,
            }
        },
        _initMethod: function () {
            $that._loadPropertyIndexMonitorAlarms();
        },
        _initMonitorAlarm: function() {},
        methods: {
            _loadPropertyIndexMonitorAlarms: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 10,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorPool.listMonitorPool',
                    param,
                    function(json, res) {
                        let _res = JSON.parse(json);

                        $that.indexMonitorAlarmInfo.monitorAlarms = _res.data;
                        $that.$nextTick(function() {
                            setInterval($that.checkPoolScroll, 2000);
                        })
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');

                    }
                );
            },
            checkPoolScroll: function() {
                let element = document.getElementById("pool");
                if (!element) { return; }
                let clientHeight = element.clientHeight;
                let scrollHeight = element.scrollHeight;
                if (scrollHeight > clientHeight) {
                    if (!$that.indexMonitorAlarmInfo.scrollInterval) {
                        $that.indexMonitorAlarmInfo.scrollInterval = setInterval($that.poolScroll, 5000);
                    }
                } else {
                    clearInterval($that.indexMonitorAlarmInfo.scrollInterval);
                    $that.indexMonitorAlarmInfo.scrollInterval = null;
                }
            },
            poolScroll: function() {
                let element = document.getElementById("pool");
                if (!element) { return; }
                var clientHeight = element.clientHeight;
                var scrollHeight = element.scrollHeight;
                var canScrollHeight = scrollHeight - clientHeight;
                // var preScrollHeight = canScrollHeight / 3.7;
                // 向下滾動
                if ($that.indexMonitorAlarmInfo.scrollTopValue <= 0) {
                    $that.indexMonitorAlarmInfo.scrollDirection = true;
                }
                if ($that.indexMonitorAlarmInfo.scrollDirection) {
                    $that.indexMonitorAlarmInfo.scrollTopValue += canScrollHeight / 1200;
                } else {
                    $that.indexMonitorAlarmInfo.scrollTopValue -= canScrollHeight / 1200;
                }
                // 觸底改變方向
                if (canScrollHeight <= $that.indexMonitorAlarmInfo.scrollTopValue) {
                    $that.indexMonitorAlarmInfo.scrollDirection = !$that.indexMonitorAlarmInfo.scrollDirection;
                }
            }
        }
    })
})(window.vc);