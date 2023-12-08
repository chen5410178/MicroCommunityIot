/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            machineDetailInfo: {
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
                _currentTab: 'machineDetailChargePort',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.machineDetailInfo.machineId = vc.getParam('machineId');
            if (!vc.notNull($that.machineDetailInfo.machineId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.machineDetailInfo._currentTab = _currentTab;
            }

            $that._loadMachineInfo();
            $that.changeTab($that.machineDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.machineDetailInfo._currentTab);
            });
        },
        methods: {
            _loadMachineInfo: function() {
                let param = {
                        params: {
                            machineId: $that.machineDetailInfo.machineId,
                            page: 1,
                            row: 10,
                            communityId: vc.getCurrentCommunity().communityId,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.machineDetailInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.machineDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    machineId: $that.machineDetailInfo.machineId,
                    machineName: $that.machineDetailInfo.machineName,
                    ruleId: $that.machineDetailInfo.ruleId,
                    chargeType: $that.machineDetailInfo.chargeType,
                })
            },
        }
    });
})(window.vc);