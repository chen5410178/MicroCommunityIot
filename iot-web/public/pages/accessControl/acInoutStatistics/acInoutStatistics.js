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
            // $that._listAccessControlInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            // $that._listAccessControlLogs();
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

                        $that._initSummaryChart(_json.data,'人员进出统计表',"acInoutStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAccessControlLogs: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.acInoutStatisticsInfo.machineId
                    }
                };
                //发送get请求
                vc.http.apiGet('/accessControl.queryAcLogStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'人员下发统计表',"acLogStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAccessControlVisits: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.acInoutStatisticsInfo.machineId
                    }
                };
                //发送get请求
                vc.http.apiGet('/accessControl.queryAcVisitStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'访客统计表',"acVisitStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAccessControlFaces: function(_page, _rows) {
                let param = {
                    params: {
                        communityId:vc.getCurrentCommunity().communityId,
                        machineId:$that.acInoutStatisticsInfo.machineId
                    }
                };
                //发送get请求
                vc.http.apiGet('/accessControl.queryAcFaceStatistics',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that._initSummaryChart(_json.data,'人脸审核统计表',"acFaceStatisticsCharts");
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchAccessControl: function(_accessControl) {
                $that.acInoutStatisticsInfo.machineId = _accessControl.machineId;
                $that._listAccessControlInouts(DEFAULT_PAGE, DEFAULT_ROWS);
                $that._listAccessControlLogs();
                $that._listAccessControlVisits();
                $that._listAccessControlFaces();
                
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