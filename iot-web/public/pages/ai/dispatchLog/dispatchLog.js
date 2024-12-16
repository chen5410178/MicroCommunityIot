/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            dispatchLogInfo: {
                dispatchLogs: [],
                machines:[],
                total: 0,
                records: 1,
                moreCondition: false,
                personId: '',
                conditions: {
                    personId: '',
                    personName: '',
                    communityId: '',
                    personLink:'',
                    machineId:''

                }
            }
        },
        _initMethod: function () {
            $that._listMachines();
            $that._listFaceLogs(DEFAULT_PAGE, DEFAULT_ROWS);

        },
        _initEvent: function () {

            vc.on('dispatchLog', 'listFaceLog', function (_param) {
                $that._listFaceLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listFaceLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMachines: function () {

                let param = {
                    params: {
                        page:1,
                        row:100,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                let _machines = [{
                    machineId:'',
                    machineName:'全部设备'
                }]

                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        _json.data.forEach(item => {
                            _machines.push(item);
                        });
                        $that.dispatchLogInfo.machines = _machines;
                        
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchMachineId:function(_community){
                $that.dispatchLogInfo.conditions.machineId = _community.machineId;
                $that._listFaceLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listFaceLogs: function (_page, _rows) {

                $that.dispatchLogInfo.conditions.page = _page;
                $that.dispatchLogInfo.conditions.row = _rows;
                $that.dispatchLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.dispatchLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/ai.getDispatchLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.dispatchLogInfo.total = _json.total;
                        $that.dispatchLogInfo.records = _json.records;
                        $that.dispatchLogInfo.dispatchLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.dispatchLogInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryFaceLogMethod: function () {
                $that._listFaceLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _viewFaceLog: function (_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
            _moreCondition: function () {
                if ($that.dispatchLogInfo.moreCondition) {
                    $that.dispatchLogInfo.moreCondition = false;
                } else {
                    $that.dispatchLogInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
