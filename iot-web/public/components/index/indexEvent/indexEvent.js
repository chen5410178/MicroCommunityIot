(function (vc) {
    vc.extends({
        data: {
            indexEventInfo: {
                events: [],
                scrollInterval: null,
                scrollTopValue: 0,
                scrollDirection: true,
            }
        },
        _initMethod: function () {
            $that._loadPropertyIndexEvents();
        },
        _initEvent: function() {},
        methods: {
            _loadPropertyIndexEvents: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 10,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/eventPool.listEventPool',
                    param,
                    function(json, res) {
                        let _res = JSON.parse(json);

                        $that.indexEventInfo.events = _res.data;
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
                    if (!$that.indexEventInfo.scrollInterval) {
                        $that.indexEventInfo.scrollInterval = setInterval($that.poolScroll, 5000);
                    }
                } else {
                    clearInterval($that.indexEventInfo.scrollInterval);
                    $that.indexEventInfo.scrollInterval = null;
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
                if ($that.indexEventInfo.scrollTopValue <= 0) {
                    $that.indexEventInfo.scrollDirection = true;
                }
                if ($that.indexEventInfo.scrollDirection) {
                    $that.indexEventInfo.scrollTopValue += canScrollHeight / 1200;
                } else {
                    $that.indexEventInfo.scrollTopValue -= canScrollHeight / 1200;
                }
                // 觸底改變方向
                if (canScrollHeight <= $that.indexEventInfo.scrollTopValue) {
                    $that.indexEventInfo.scrollDirection = !$that.indexEventInfo.scrollDirection;
                }
            }
        }
    })
})(window.vc);