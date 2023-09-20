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
                }
            }
        },
        _initMethod: function () {
            vc.component._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('liftMachineReservationManage', 'listLiftMachineReservation', function (_param) {
                vc.component._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listLiftMachineReservations(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listLiftMachineReservations: function (_page, _rows) {

                vc.component.liftMachineReservationManageInfo.conditions.page = _page;
                vc.component.liftMachineReservationManageInfo.conditions.row = _rows;
                var param = {
                    params: vc.component.liftMachineReservationManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('liftMachineReservation.listLiftMachineReservation',
                    param,
                    function (json, res) {
                        var _liftMachineReservationManageInfo = JSON.parse(json);
                        vc.component.liftMachineReservationManageInfo.total = _liftMachineReservationManageInfo.total;
                        vc.component.liftMachineReservationManageInfo.records = _liftMachineReservationManageInfo.records;
                        vc.component.liftMachineReservationManageInfo.liftMachineReservations = _liftMachineReservationManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.liftMachineReservationManageInfo.records,
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
                vc.component._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            liftMachineReservationManage: function () {
                $that.liftMachineReservationManageInfo.conditions.machineId = '';
                $that.liftMachineReservationManageInfo.conditions.resWay = '';
                $that.liftMachineReservationManageInfo.conditions.personWay = '';
                $that.liftMachineReservationManageInfo.conditions.personId = '';
                $that.liftMachineReservationManageInfo.conditions.roomId = '';
                $that.liftMachineReservationManageInfo.conditions.createTime = '';
                $that._listLiftMachineReservations(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if (vc.component.liftMachineReservationManageInfo.moreCondition) {
                    vc.component.liftMachineReservationManageInfo.moreCondition = false;
                } else {
                    vc.component.liftMachineReservationManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
