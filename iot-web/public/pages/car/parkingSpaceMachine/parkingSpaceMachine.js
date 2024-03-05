/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingSpaceMachineInfo: {
                parkingSpaceMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineCode: '',
                    machineName: '',
                    implBean: '',
                }
            }
        },
        _initMethod: function () {
            $that._listParkingSpaceMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('parkingSpaceMachine', 'listParkingSpaceMachine', function (_param) {
                $that._listParkingSpaceMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listParkingSpaceMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingSpaceMachines: function (_page, _rows) {

                $that.parkingSpaceMachineInfo.conditions.page = _page;
                $that.parkingSpaceMachineInfo.conditions.row = _rows;
                $that.parkingSpaceMachineInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.parkingSpaceMachineInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/parkingSpaceMachine.listParkingSpaceMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingSpaceMachineInfo.total = _json.total;
                        $that.parkingSpaceMachineInfo.records = _json.records;
                        $that.parkingSpaceMachineInfo.parkingSpaceMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingSpaceMachineInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddParkingSpaceMachineModal: function () {
                vc.jumpToPage('/#/pages/car/addParkingSpaceMachine')
            },
            _openEditParkingSpaceMachineModel: function (_parkingSpaceMachine) {
                vc.jumpToPage('/#/pages/car/editParkingSpaceMachine?machineId='+_parkingSpaceMachine.machineId)
            },
            _openDeleteParkingSpaceMachineModel: function (_parkingSpaceMachine) {
                vc.emit('deleteParkingSpaceMachine', 'openDeleteParkingSpaceMachineModal', _parkingSpaceMachine);
            },
            _queryParkingSpaceMachineMethod: function () {
                $that._listParkingSpaceMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.parkingSpaceMachineInfo.moreCondition) {
                    $that.parkingSpaceMachineInfo.moreCondition = false;
                } else {
                    $that.parkingSpaceMachineInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
