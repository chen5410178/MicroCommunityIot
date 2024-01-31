/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftRideLogInfo: {
                liftRideLogs: [],
                machines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                logId: '',
                conditions: {
                    machineId: '',
                    acMachineName: '',
                    acMachineCode: '',
                    rideType: '',
                    name: '',
                    personType: '',

                }
            }
        },
        _initMethod: function () {
            $that._listLift();
            $that._listLiftRideLogs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftRideLog', 'listLiftRideLog', function (_param) {
                $that._listLiftRideLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftRideLogs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLift: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                let _machines = [{
                    machineId:'',
                    machineName:'全部'
                }];
                //发送get请求
                vc.http.apiGet('/liftMachine.listLiftMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        _json.data.forEach(item => {
                            _machines.push(item);
                        });
                        $that.liftRideLogInfo.machines = _machines;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _swatchLiftMachine: function (_lift) {
                $that.liftRideLogInfo.conditions.machineId = _lift.machineId;
                $that._listLiftRideLogs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listLiftRideLogs: function (_page, _rows) {

                $that.liftRideLogInfo.conditions.page = _page;
                $that.liftRideLogInfo.conditions.row = _rows;
                $that.liftRideLogInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.liftRideLogInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftMachineLog.listLiftRideLog',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftRideLogInfo.total = _json.total;
                        $that.liftRideLogInfo.records = _json.records;
                        $that.liftRideLogInfo.liftRideLogs = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftRideLogInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryLiftRideLogMethod: function () {
                $that._listLiftRideLogs(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.liftRideLogInfo.moreCondition) {
                    $that.liftRideLogInfo.moreCondition = false;
                } else {
                    $that.liftRideLogInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
