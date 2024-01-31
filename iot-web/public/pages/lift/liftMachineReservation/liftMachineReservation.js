/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineReservationInfo: {
                liftMachineReservations: [],
                machines:[],
                total: 0,
                records: 1,
                moreCondition: false,
                lmrId: '',
                conditions: {
                    machineId: '',
                    resWay: '',
                    personWay: '',
                    personId: '',
                    roomId: '',
                    createTime: '',
                    queryStartTime:'',
                    queryEndTime:''
                }
            }
        },
        _initMethod: function () {
            $that._listLift();
            $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.liftMachineReservationInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.liftMachineReservationInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('liftMachineReservation', 'listLiftMachineReservation', function (_param) {
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftMachineReservations(_currentPage, DEFAULT_ROWS);
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
                        $that.liftMachineReservationInfo.machines = _machines;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _swatchLiftMachine: function (_lift) {
                $that.liftMachineReservationInfo.conditions.machineId = _lift.machineId;
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listLiftMachineReservations: function (_page, _rows) {

                $that.liftMachineReservationInfo.conditions.page = _page;
                $that.liftMachineReservationInfo.conditions.row = _rows;
                let param = {
                    params: $that.liftMachineReservationInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftMachineReservation.listLiftMachineReservation',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineReservationInfo.total = _json.total;
                        $that.liftMachineReservationInfo.records = _json.records;
                        $that.liftMachineReservationInfo.liftMachineReservations = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftMachineReservationInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
      
            _queryLiftMachineReservationMethod: function () {
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _resetLiftMachineReservationMethod: function () {
                $that.liftMachineReservationInfo.conditions.machineId = '';
                $that.liftMachineReservationInfo.conditions.resWay = '';
                $that.liftMachineReservationInfo.conditions.personWay = '';
                $that.liftMachineReservationInfo.conditions.personId = '';
                $that.liftMachineReservationInfo.conditions.roomId = '';
                $that.liftMachineReservationInfo.conditions.createTime = '';
                $that.liftMachineReservationInfo.conditions.queryStartTime = '';
                $that.liftMachineReservationInfo.conditions.queryEndTime = '';
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.liftMachineReservationInfo.moreCondition) {
                    $that.liftMachineReservationInfo.moreCondition = false;
                } else {
                    $that.liftMachineReservationInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
