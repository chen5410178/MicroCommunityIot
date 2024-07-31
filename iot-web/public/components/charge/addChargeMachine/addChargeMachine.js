(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addChargeMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                implBean: '',
                ruleId: '',
                energyPrice: '1',
                factorys: [],
                specs: [],
                rules: [],
                portCount: '',
                chargeType: '',
                monitors: [],
                monitorId: ''
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('addChargeMachine', 'openAddChargeMachineModal', function () {
                $that._listFactorys();
                $that._listAddMonitors();
                $('#addChargeMachineModel').modal('show');
            });
        },
        methods: {
            addChargeMachineValidate() {
                return vc.validate.validate({
                    addChargeMachineInfo: $that.addChargeMachineInfo
                }, {
                    'addChargeMachineInfo.machineName': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "名称不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "200",
                            errInfo: "名称不能超过200"
                        },
                    ],
                    'addChargeMachineInfo.machineCode': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备编号不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "设备编号不能超过30"
                        },
                    ],
                    'addChargeMachineInfo.implBean': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "厂家不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "30",
                            errInfo: "厂家不能超过30"
                        },
                    ],
                    'addChargeMachineInfo.ruleId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小时电价不能为空"
                        }
                    ],
                    'addChargeMachineInfo.energyPrice': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "用量电价不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "12",
                            errInfo: "用量电价不能超过12"
                        },
                    ],
                    'addChargeMachineInfo.chargeType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "充电类型不能为空"
                        }
                    ],

                });
            },
            saveChargeMachineInfo: function () {
                if (!$that.addChargeMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                $that.addChargeMachineInfo.communityId = vc.getCurrentCommunity().communityId;

                vc.http.apiPost(
                    '/chargeMachine.saveChargeMachine',
                    JSON.stringify($that.addChargeMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addChargeMachineModel').modal('hide');
                            $that.clearAddChargeMachineInfo();
                            vc.emit('chargeMachineManage', 'listChargeMachine', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            _loadFees: function (event) {
                const selectedValue = event.target.value;
                $that._listAddChargeRules(selectedValue);
            },
            clearAddChargeMachineInfo: function () {
                $that.addChargeMachineInfo = {
                    machineName: '',
                    machineCode: '',
                    implBean: '',
                    ruleId: '',
                    energyPrice: '1',
                    factorys: [],
                    specs: [],
                    rules: [],
                    portCount: '',
                    chargeType: '',
                    monitors: [],
                    monitorId: ''
                };
            },
            _listFactorys: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 500,
                    }
                };
                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineFactory', param,
                    function (json, res) {
                        let _feeConfigManageInfo = JSON.parse(json);
                        $that.addChargeMachineInfo.factorys = _feeConfigManageInfo.data;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _changeFactory: function () {
                let _factorys = $that.addChargeMachineInfo.factorys;
                _factorys.forEach(item => {
                    if (item.factoryId == $that.addChargeMachineInfo.implBean) {
                        item.specs.forEach(specItem => {
                            specItem.specValue = "";
                        })
                        $that.addChargeMachineInfo.specs = item.specs;
                    }
                });
            },
            _listAddChargeRules: function (_chargeType) {
                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        chargeType: _chargeType,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/chargeRule.listChargeRule',
                    param,
                    function (json, res) {
                        let _chargeRuleManageInfo = JSON.parse(json);
                        $that.addChargeMachineInfo.rules = _chargeRuleManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listAddMonitors: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorMachine.listMonitorMachine',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.addChargeMachineInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);
