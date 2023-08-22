(function (vc) {

    vc.extends({
        propTypes: {
            callBackListener: vc.propTypes.string, //父组件名称
            callBackFunction: vc.propTypes.string //父组件监听方法
        },
        data: {
            addChargeRuleFeeInfo: {
                crfId: '',
                ruleId: '',
                minEnergyPrice: '',
                maxEnergyPrice: '',
                durationPrice: '',
                remark: '',
                chargeType: '',
                startTime: '',
                endTime: ''
            }
        },
        _initMethod: function () {
            vc.initHourMinute('addStartTime', function (_value) {
                vc.component.addChargeRuleFeeInfo.startTime = _value;
            });
            vc.initHourMinute('addEndTime', function (_value) {
                vc.component.addChargeRuleFeeInfo.endTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('addChargeRuleFee', 'openAddChargeRuleFeeModal', function (_param) {
                vc.copyObject(_param,$that.addChargeRuleFeeInfo);
                if ($that.addChargeRuleFeeInfo.chargeType !== '1002') {
                    document.querySelectorAll('.addTime')[0].setAttribute('style', "display:none");
                } else {
                    document.querySelectorAll('.addTime')[0].removeAttribute('style');
                    $that.addChargeRuleFeeInfo.minEnergyPrice = null;
                    $that.addChargeRuleFeeInfo.maxEnergyPrice = null;
                }
                $('#addChargeRuleFeeModel').modal('show');
            });
        },
        methods: {
            addChargeRuleFeeValidate() {
                if ($that.addChargeRuleFeeInfo.chargeType === '1001') {
                    return vc.validate.validate({
                        addChargeRuleFeeInfo: vc.component.addChargeRuleFeeInfo
                    }, {
                        'addChargeRuleFeeInfo.chargeType': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "充电类型不能为空"
                            }
                        ],
                        'addChargeRuleFeeInfo.ruleId': [
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
                        'addChargeRuleFeeInfo.minEnergyPrice': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "最小功率不能为空"
                            },
                            {
                                limit: "maxLength",
                                param: "30",
                                errInfo: "最小功率不能超过30"
                            },
                        ],
                        'addChargeRuleFeeInfo.maxEnergyPrice': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "最大功率不能为空"
                            },
                            {
                                limit: "maxLength",
                                param: "30",
                                errInfo: "最大功率不能超过30"
                            },
                        ],
                        'addChargeRuleFeeInfo.durationPrice': [
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
                        'addChargeRuleFeeInfo.remark': [
                            {
                                limit: "maxLength",
                                param: "512",
                                errInfo: "备注不能超过512"
                            },
                        ],
                    });
                } else if ($that.addChargeRuleFeeInfo.chargeType === '1002') {
                    return vc.validate.validate({
                        addChargeRuleFeeInfo: vc.component.addChargeRuleFeeInfo
                    }, {
                        'addChargeRuleFeeInfo.chargeType': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "充电类型不能为空"
                            }
                        ],
                        'addChargeRuleFeeInfo.ruleId': [
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
                        'addChargeRuleFeeInfo.startTime': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "时段开始时间不能为空"
                            }
                        ],
                        'addChargeRuleFeeInfo.endTime': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "时段结束时间不能为空"
                            }
                        ],
                        'addChargeRuleFeeInfo.durationPrice': [
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
                        'addChargeRuleFeeInfo.remark': [
                            {
                                limit: "maxLength",
                                param: "512",
                                errInfo: "备注不能超过512"
                            },
                        ],
                    });
                }

            },
            saveChargeRuleFeeInfo: function () {
                if (!vc.component.addChargeRuleFeeValidate()) {
                    vc.toast(vc.validate.errInfo);

                    return;
                }

                vc.component.addChargeRuleFeeInfo.communityId = vc.getCurrentCommunity().communityId;
          
                vc.http.apiPost(
                    '/chargeRule.saveChargeRuleFee',
                    JSON.stringify(vc.component.addChargeRuleFeeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addChargeRuleFeeModel').modal('hide');
                            vc.component.clearAddChargeRuleFeeInfo();
                            vc.emit('chargeRuleFee', 'listChargeRuleFee', {});

                            return;
                        }
                        vc.message(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.message(errInfo);

                    });
            },
            clearAddChargeRuleFeeInfo: function () {
                vc.component.addChargeRuleFeeInfo = {
                    ruleId: '',
                    minEnergyPrice: '',
                    maxEnergyPrice: '',
                    durationPrice: '',
                    remark: '',
                    chargeType: '',
                    startTime: '',
                    endTime: ''
                };
            }
        }
    });

})(window.vc);
