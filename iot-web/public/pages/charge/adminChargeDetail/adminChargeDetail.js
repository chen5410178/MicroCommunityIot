/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            adminChargeDetailInfo: {
                machineId: "",
                machineName: "",
                machineCode: "",
                factoryName: "",
                chargeType: "",
                chargeTypeName: "",
                portCount: "",
                ruleId: "",
                ruleName: "",
                stateName: "",
                heartbeatTime: "",
                createTime: "",
                orderId: "",
                portId: "",
                acctId: "",
                _currentTab: 'adminChargeDetailPort',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.adminChargeDetailInfo.machineId = vc.getParam('machineId');
            $that.adminChargeDetailInfo.orderId = vc.getParam('orderId');
            $that.adminChargeDetailInfo.portId = vc.getParam('portId');
            $that.adminChargeDetailInfo.acctId = vc.getParam('acctId');
            if (!vc.notNull($that.adminChargeDetailInfo.machineId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.adminChargeDetailInfo._currentTab = _currentTab;
            }

            $that._loadMachineInfo();
            $that.changeTab($that.adminChargeDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.adminChargeDetailInfo._currentTab);
            });
        },
        methods: {
            _loadMachineInfo: function() {
                let param = {
                        params: {
                            machineId: $that.adminChargeDetailInfo.machineId,
                            page: 1,
                            row: 10,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/chargeMachine.listAdminChargeMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.adminChargeDetailInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.adminChargeDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    machineId: $that.adminChargeDetailInfo.machineId,
                    machineName: $that.adminChargeDetailInfo.machineName,
                    ruleId: $that.adminChargeDetailInfo.ruleId,
                    chargeType: $that.adminChargeDetailInfo.chargeType,
                    orderId: $that.adminChargeDetailInfo.orderId,
                    portId: $that.adminChargeDetailInfo.portId,
                    acctId: $that.adminChargeDetailInfo.acctId,
                })
            },
        }
    });
})(window.vc);