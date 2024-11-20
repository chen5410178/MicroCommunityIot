/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeMachinePortManageInfo: {
                chargeMachinePorts: [],
                total: 0,
                records: 1,
                moreCondition: false,
                portId: '',
                conditions: {
                    machineId: '',
                    portName: '',
                    portCode: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            },
            chargeMachineInfo: {

            }
        },
        _initMethod: function () {
            $that.chargeMachinePortManageInfo.conditions.machineId = vc.getParam('machineId');
            $that._listChargeMachinePorts(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listChargeMachines();
        },
        _initEvent: function () {

            vc.on('chargeMachinePortManage', 'listChargeMachinePort', function (_param) {
                $that._listChargeMachinePorts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listChargeMachinePorts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachinePorts: function (_page, _rows) {

                $that.chargeMachinePortManageInfo.conditions.page = _page;
                $that.chargeMachinePortManageInfo.conditions.row = _rows;
                var param = {
                    params: $that.chargeMachinePortManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachinePort',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeMachinePortManageInfo.total = _json.total;
                        $that.chargeMachinePortManageInfo.records = _json.records;
                        $that.chargeMachinePortManageInfo.chargeMachinePorts = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeMachinePortManageInfo.records,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listChargeMachines: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: vc.getParam('machineId')
                    }
                };
                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeMachineInfo = _json.data[0];
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeMachinePortModal: function () {
                vc.emit('addChargeMachinePort', 'openAddChargeMachinePortModal', {
                    machineId: $that.chargeMachinePortManageInfo.conditions.machineId
                });
            },
            _showStopCharge: function (_order) {
                vc.emit('stopChargeMachine', 'openStopChargeMachineModal', _order);
            },
            _openDeleteChargeMachinePortModel: function (_chargeMachinePort) {
                vc.emit('deleteChargeMachinePort', 'openDeleteChargeMachinePortModal', _chargeMachinePort);
            },
            _queryChargeMachinePortMethod: function () {
                $that._listChargeMachinePorts(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _chargeMachineQrCode: function (_chargeMachinePort) {
                let _qrCode = $that.chargeMachineInfo.qrCode + '/' + _chargeMachinePort.portId;
                if ($that.chargeMachineInfo.chargeType == '') {
                    let _qrCodeNew = _qrCode.replace("machineToCarCharge", "carChargePort");
                    _qrCode = _qrCodeNew;
                }
                let qrCode = {
                    qrCode: _qrCode,
                    machineName: $that.chargeMachineInfo.machineName + _chargeMachinePort.portName
                };
                vc.emit('chargeMachineQrCode', 'openChargeMachineQrCodeModal', qrCode);
            },
            _moreCondition: function () {
                if ($that.chargeMachinePortManageInfo.moreCondition) {
                    $that.chargeMachinePortManageInfo.moreCondition = false;
                } else {
                    $that.chargeMachinePortManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);