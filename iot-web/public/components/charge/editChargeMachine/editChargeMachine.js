(function (vc, vm) {

    vc.extends({
        data: {
            editChargeMachineInfo: {
                machineId: '',
                machineName: '',
                machineCode: '',
                implBean: '',
                ruleId: '',
                energyPrice: '1',
                factorys:[],
                rules:[],
                specs: [],
                chargeType: '',
                monitors:[],
                monitorId:''
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('editChargeMachine', 'openEditChargeMachineModal', function (_params) {
                $that.refreshEditChargeMachineInfo();
                $that._listEditFactorys();
                $that._listEditMonitors();
                $('#editChargeMachineModel').modal('show');
                vc.copyObject(_params, $that.editChargeMachineInfo);
                $that._listEditChargeRules($that.editChargeMachineInfo.chargeType);
                $that.editChargeMachineInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editChargeMachineValidate: function () {
                return vc.validate.validate({
                    editChargeMachineInfo: $that.editChargeMachineInfo
                }, {
                    'editChargeMachineInfo.machineName': [
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
                    'editChargeMachineInfo.machineCode': [
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
                    'editChargeMachineInfo.implBean': [
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
                    'editChargeMachineInfo.ruleId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "小时电价不能为空"
                        },
                    ],
                    'editChargeMachineInfo.energyPrice': [
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
                    'editChargeMachineInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }],
                    'editChargeMachineInfo.chargeType': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "充电类型不能为空"
                        }
                    ],

                });
            },
            editChargeMachine: function () {
                if (!$that.editChargeMachineValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/chargeMachine.updateChargeMachine',
                    JSON.stringify($that.editChargeMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editChargeMachineModel').modal('hide');
                            vc.emit('chargeMachineManage', 'listChargeMachine', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);
                    });
            },
            refreshEditChargeMachineInfo: function () {
                $that.editChargeMachineInfo = {
                    machineId: '',
                    machineName: '',
                    machineCode: '',
                    implBean: '',
                    ruleId: '',
                    energyPrice: '1',
                    factorys:[],
                    rules:[],
                    specs: [],
                    chargeType: '',
                    monitors:[],
                    monitorId:''
                }
            },
            _listEditFactorys: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 500,
                    }
                };
                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineFactory', param,
                    function(json, res) {
                        let _feeConfigManageInfo = JSON.parse(json);
                        $that.editChargeMachineInfo.factorys = _feeConfigManageInfo.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _changeEditFactory: function() {
                let _factorys = $that.editChargeMachineInfo.factorys;
                _factorys.forEach(item => {
                    if (item.factoryId == $that.editChargeMachineInfo.implBean) {
                        item.specs.forEach(specItem => {
                            specItem.specValue = "";
                        })
                        $that.editChargeMachineInfo.specs = item.specs;
                    }
                });
            },
            _loadEditFees: function (event) {
                const selectedValue = event.target.value;
                $that._listEditChargeRules(selectedValue);
            },
            _listEditChargeRules: function (_chargeType) {
                let param = {
                    params: {
                        page:1,
                        row:100,
                        chargeType:_chargeType,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/chargeRule.listChargeRule',
                    param,
                    function (json, res) {
                        let _chargeRuleManageInfo = JSON.parse(json);
                        $that.editChargeMachineInfo.rules = _chargeRuleManageInfo.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listEditMonitors: function (_page, _rows) {
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
                        $that.editChargeMachineInfo.monitors = _json.data;

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc, window.$that);
