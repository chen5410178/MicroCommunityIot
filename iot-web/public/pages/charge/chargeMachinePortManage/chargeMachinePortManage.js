/**
    入驻小区
**/
(function(vc) {
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
        _initMethod: function() {
            $that.chargeMachinePortManageInfo.conditions.machineId = vc.getParam('machineId');
            vc.component._listChargeMachinePorts(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.component._listChargeMachines();
        },
        _initEvent: function() {

            vc.on('chargeMachinePortManage', 'listChargeMachinePort', function(_param) {
                vc.component._listChargeMachinePorts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                vc.component._listChargeMachinePorts(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachinePorts: function(_page, _rows) {

                vc.component.chargeMachinePortManageInfo.conditions.page = _page;
                vc.component.chargeMachinePortManageInfo.conditions.row = _rows;
                var param = {
                    params: vc.component.chargeMachinePortManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachinePort',
                    param,
                    function(json, res) {
                        var _chargeMachinePortManageInfo = JSON.parse(json);
                        vc.component.chargeMachinePortManageInfo.total = _chargeMachinePortManageInfo.total;
                        vc.component.chargeMachinePortManageInfo.records = _chargeMachinePortManageInfo.records;
                        vc.component.chargeMachinePortManageInfo.chargeMachinePorts = _chargeMachinePortManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.chargeMachinePortManageInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listChargeMachines: function() {
                let param = {
                    params: {page:1,row:1,communityId:vc.getCurrentCommunity().communityId,machineId:vc.getParam('machineId')}
                };
                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeMachineInfo = _json.data[0];
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeMachinePortModal: function() {
                vc.emit('addChargeMachinePort', 'openAddChargeMachinePortModal', {
                    machineId: $that.chargeMachinePortManageInfo.conditions.machineId
                });
            },
            _showStopCharge: function(_order) {
                vc.emit('stopChargeMachine', 'openStopChargeMachineModal', _order);
            },
            _openDeleteChargeMachinePortModel: function(_chargeMachinePort) {
                vc.emit('deleteChargeMachinePort', 'openDeleteChargeMachinePortModal', _chargeMachinePort);
            },
            _queryChargeMachinePortMethod: function() {
                vc.component._listChargeMachinePorts(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _chargeMachineQrCode: function(_chargeMachinePort) {
                let _qrCode = $that.chargeMachineInfo.qrCode+ '&portId='+ _chargeMachinePort.portId;
                if ($that.chargeMachineInfo.chargeType == ''){
                    let _qrCodeNew = _qrCode.replace("machineToCarCharge", "carChargePort");
                    _qrCode = _qrCodeNew;
                }
                let qrCode = { qrCode: _qrCode,machineName:  $that.chargeMachineInfo.machineName + _chargeMachinePort.portName};
                vc.emit('chargeMachineQrCode', 'openChargeMachineQrCodeModal', qrCode);
            },
            _moreCondition: function() {
                if (vc.component.chargeMachinePortManageInfo.moreCondition) {
                    vc.component.chargeMachinePortManageInfo.moreCondition = false;
                } else {
                    vc.component.chargeMachinePortManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);