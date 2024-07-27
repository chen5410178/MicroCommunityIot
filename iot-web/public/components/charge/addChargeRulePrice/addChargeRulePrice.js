(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addChargeRulePriceInfo: {
                crpId: '',
                ruleId: '',
                price: '',
                servicePrice: '',
                remark: '',
                chargeType: '',
                startTime: '',
                endTime: ''
            }
        },
        _initMethod: function () {
            vc.initHourMinute('addPriceStartTime', function (_value) {
                vc.component.addChargeRulePriceInfo.startTime = _value;
            });
            vc.initHourMinute('addPriceEndTime', function (_value) {
                vc.component.addChargeRulePriceInfo.endTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('addChargeRulePrice', 'openAddChargeRulePriceModal', function (_param) {
                vc.copyObject(_param,$that.addChargeRulePriceInfo);
                $('#addChargeRulePriceModel').modal('show');
            });
        },
        methods: {
            addChargeRulePriceValidate() {
                    return vc.validate.validate({
                        addChargeRulePriceInfo: vc.component.addChargeRulePriceInfo
                    }, {
                        'addChargeRulePriceInfo.chargeType': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "充电类型不能为空"
                            }
                        ],
                        'addChargeRulePriceInfo.ruleId': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "规则ID不能为空"
                            },
                            {
                                limit: "maxLength",
                                param: "30",
                                errInfo: "规则ID不能超过30"
                            },
                        ],
                        'addChargeRulePriceInfo.startTime': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "时段开始时间不能为空"
                            }
                        ],
                        'addChargeRulePriceInfo.endTime': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "时段结束时间不能为空"
                            }
                        ],
                        'addChargeRulePriceInfo.price': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "小时电价不能为空"
                            },
                            {
                                limit: "maxLength",
                                param: "30",
                                errInfo: "小时电价不能超过30"
                            },
                        ],
                        'addChargeRulePriceInfo.servicePrice': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "服务单价不能为空"
                            },
                            {
                                limit: "maxLength",
                                param: "30",
                                errInfo: "服务单价不能超过30"
                            },
                        ],
                        'addChargeRulePriceInfo.remark': [
                            {
                                limit: "maxLength",
                                param: "512",
                                errInfo: "备注不能超过512"
                            },
                        ],
                    });
            },
            saveChargeRulePriceInfo: function () {
                if (!vc.component.addChargeRulePriceValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.component.addChargeRulePriceInfo.communityId = vc.getCurrentCommunity().communityId;
          
                vc.http.apiPost(
                    '/chargeRule.saveChargeRulePrice',
                    JSON.stringify(vc.component.addChargeRulePriceInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addChargeRulePriceModel').modal('hide');
                            vc.component.clearAddChargeRulePriceInfo();
                            vc.emit('chargeRulePrice', 'listChargeRulePrice', {});

                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddChargeRulePriceInfo: function () {
                vc.component.addChargeRulePriceInfo = {
                    ruleId: '',
                    price: '',
                    servicePrice: '',
                    remark: '',
                    chargeType: '',
                    startTime: '',
                    endTime: ''
                };
            }
        }
    });

})(window.vc);
