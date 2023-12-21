/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            acInoutStatisticsInfo: {
                acInoutStatisticss: [],
                accessControls: [],
                records: 1,
                machineId:'',
            }
        },
        _initMethod: function() {
            $that._listAccessControls();
            $that._listAccessControlInouts(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

          
        },
        methods: {
            _listAccessControlInouts: function(_page, _rows) {

                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.acInoutStatisticsInfo.machineId
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControl.queryAcInoutStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            swatchAccessControl: function(_accessControl) {
                $that.acInoutStatisticsInfo.machineId = _accessControl.machineId;
                $that._listAccessControlInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listAccessControls: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.acInoutStatisticsInfo.accessControls = [];

                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.acInoutStatisticsInfo.accessControls = _json.data;
                        if(_json.data && _json.data.length>0){
                            $that.swatchAccessControl(_json.data[0]);
                        }
                        
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _initSummaryChart: function(_data) {
                let dom = document.getElementById("acInoutStatisticsCharts");
                let myChart = echarts.init(dom);
                let _createTime = [];
                let _realChargeTotals = [];
                _data.forEach(item => {
                    _createTime.push(item.createTime);
                    _realChargeTotals.push(item.inoutCount);
                });
                let option = null;
                option = {
                    title: {
                        text: '人员进出统计表'
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
                        name: '收入金额',
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