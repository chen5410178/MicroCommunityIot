/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            telMachineInfo: {
                telMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineId: '',
                    machineCode: '',
                    machineName: '',
                }
            }
        },
        _initMethod: function () {
            $that._listTelMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('telMachine', 'listTelMachine', function (_param) {
                $that._listTelMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listTelMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTelMachines: function (_page, _rows) {

                $that.telMachineInfo.conditions.page = _page;
                $that.telMachineInfo.conditions.row = _rows;
                $that.telMachineInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.telMachineInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/telMachine.listTelMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telMachineInfo.total = _json.total;
                        $that.telMachineInfo.records = _json.records;
                        $that.telMachineInfo.telMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.telMachineInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddTelMachineModal: function () {
                vc.emit('addTelMachine', 'openAddTelMachineModal', {});
            },
            _openEditTelMachineModel: function (_telMachine) {
                vc.emit('editTelMachine', 'openEditTelMachineModal', _telMachine);
            },
            _openDeleteTelMachineModel: function (_telMachine) {
                vc.emit('deleteTelMachine', 'openDeleteTelMachineModal', _telMachine);
            },
            _queryTelMachineMethod: function () {
                $that._listTelMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.telMachineInfo.moreCondition) {
                    $that.telMachineInfo.moreCondition = false;
                } else {
                    $that.telMachineInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
