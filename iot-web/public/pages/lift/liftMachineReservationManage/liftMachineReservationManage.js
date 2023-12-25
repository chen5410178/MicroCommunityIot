/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            liftMachineReservationManageInfo: {
                liftMachineReservations: [],
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
            $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.liftMachineReservationManageInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.liftMachineReservationManageInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('liftMachineReservationManage', 'listLiftMachineReservation', function (_param) {
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listLiftMachineReservations(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineReservations: function (_page, _rows) {

                $that.liftMachineReservationManageInfo.conditions.page = _page;
                $that.liftMachineReservationManageInfo.conditions.row = _rows;
                let param = {
                    params: $that.liftMachineReservationManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/liftMachineReservation.listLiftMachineReservation',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.liftMachineReservationManageInfo.total = _json.total;
                        $that.liftMachineReservationManageInfo.records = _json.records;
                        $that.liftMachineReservationManageInfo.liftMachineReservations = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.liftMachineReservationManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            /*_openAddLiftMachineReservationModal: function () {
                vc.emit('addLiftMachineReservation', 'openAddLiftMachineReservationModal', {});
            },
            _openEditLiftMachineReservationModel: function (_liftMachineReservation) {
                vc.emit('editLiftMachineReservation', 'openEditLiftMachineReservationModal', _liftMachineReservation);
            },
            _openDeleteLiftMachineReservationModel: function (_liftMachineReservation) {
                vc.emit('deleteLiftMachineReservation', 'openDeleteLiftMachineReservationModal', _liftMachineReservation);
            },*/
            _queryLiftMachineReservationMethod: function () {
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _resetLiftMachineReservationMethod: function () {
                $that.liftMachineReservationManageInfo.conditions.machineId = '';
                $that.liftMachineReservationManageInfo.conditions.resWay = '';
                $that.liftMachineReservationManageInfo.conditions.personWay = '';
                $that.liftMachineReservationManageInfo.conditions.personId = '';
                $that.liftMachineReservationManageInfo.conditions.roomId = '';
                $that.liftMachineReservationManageInfo.conditions.createTime = '';
                $that.liftMachineReservationManageInfo.conditions.queryStartTime = '';
                $that.liftMachineReservationManageInfo.conditions.queryEndTime = '';
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.liftMachineReservationManageInfo.moreCondition) {
                    $that.liftMachineReservationManageInfo.moreCondition = false;
                } else {
                    $that.liftMachineReservationManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
