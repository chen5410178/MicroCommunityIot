/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterStatisitcsInfo: {
                meterTypes: [],
                meterType:'',
                records: 1,
            }
        },
        _initMethod: function() {
            $that._listMeterType();
            $that._listMeterCountStatistics();
            $that._listMeterMoneyStatistics();
        },
        _initEvent: function() {

          
        },
        methods: {
            _listMeterCountStatistics: function(_page, _rows) {

                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.meterStatisitcsInfo.machineId,
                        meterType:$that.meterStatisitcsInfo.meterType
                    }
                };

                //发送get请求
                vc.http.apiGet('/meter.queryMeterCountStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);

                        $that._initSummaryChart(_json.data,'充电订单统计',"meterCountStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listMeterMoneyStatistics: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.meterStatisitcsInfo.machineId,
                        meterType:$that.meterStatisitcsInfo.meterType
                    }
                };
                //发送get请求
                vc.http.apiGet('/meter.queryMeterMoneyStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'充电金额统计',"meterMoneyStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listMeterType: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 500,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/meterType.listMeterType',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.meterStatisitcsInfo.meterTypes = [{
                            typeName: '水电类型',
                            typeId: ''
                        }];
                        _json.data.forEach(item => {
                            $that.meterStatisitcsInfo.meterTypes.push(item);
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            switchMeterType: function (_meterType) {
                $that.meterStatisitcsInfo.meterType = _meterType.typeId;
                $that._listMeterCountStatistics();
                $that._listMeterMoneyStatistics();
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