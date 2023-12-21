/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeStatisticsInfo: {
                chargeStatisticss: [],
                records: 1,
                machineId:'',
            }
        },
        _initMethod: function() {
            $that._listOrderCountStatistics();
            $that._listOrderMoneyStatistics();
            $that._listMonthCountStatistics();
            $that._listMonthMoneyStatistics();
        },
        _initEvent: function() {

          
        },
        methods: {
            _listOrderCountStatistics: function(_page, _rows) {

                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.chargeStatisticsInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/charge.queryOrderCountStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);

                        $that._initSummaryChart(_json.data,'充电订单统计',"orderCountStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listOrderMoneyStatistics: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.chargeStatisticsInfo.machineId
                    }
                };
                //发送get请求
                vc.http.apiGet('/charge.queryOrderMoneyStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'充电金额统计',"orderMoneyStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listMonthCountStatistics: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.chargeStatisticsInfo.machineId
                    }
                };
                //发送get请求
                vc.http.apiGet('/charge.queryMonthCountStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'月卡订单统计',"monthCountStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listMonthMoneyStatistics: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.chargeStatisticsInfo.machineId
                    }
                };
                //发送get请求
                vc.http.apiGet('/charge.queryMonthMoneyStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'月卡金额统计',"monthMoneyStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            
            _initSummaryChart: function(_data,_title,_elementName) {
                let dom = document.getElementById(_elementName);
                let myChart = echarts.init(dom);
                let _createTime = [];
                let _realChargeTotals = [];
                _data.forEach(item => {
                    _createTime.push(item.createTime);
                    _realChargeTotals.push(item.totalCount);
                });
                let option = null;
                option = {
                    title: {
                        text: _title
                    },
                    tooltip: {
                        trigger: 'axis'
                    },
                    legend: {
                        data: _createTime
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    toolbox: {
                        feature: {
                            saveAsImage: {}
                        }
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: _createTime
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [{
                        name: '数量',
                        type: 'line',
                        stack: 'Total',
                        data: _realChargeTotals
                    }]
                };
                if (option && typeof option === "object") {
                    myChart.setOption(option, true);
                }
            }

        }
    });
})(window.vc);