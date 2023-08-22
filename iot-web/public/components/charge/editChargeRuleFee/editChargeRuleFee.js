(function (vc, vm) {

    vc.extends({
        data: {
            editChargeRuleFeeInfo: {
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
            vc.initHourMinute('editStartTime', function (_value) {
                vc.component.editChargeRuleFeeInfo.startTime = _value;
            });
            vc.initHourMinute('editEndTime', function (_value) {
                vc.component.editChargeRuleFeeInfo.endTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('editChargeRuleFee', 'openEditChargeRuleFeeModal', function (_params) {
                vc.component.refreshEditChargeRuleFeeInfo();
                vc.copyObject(_params, vc.component.editChargeRuleFeeInfo);
                if ($that.editChargeRuleFeeInfo.chargeType !== '1002') {
                    document.querySelectorAll('.editTime')[0].setAttribute('style', "display:none");
                } else {
                    document.querySelectorAll('.editTime')[0].removeAttribute('style');
                    $that.editChargeRuleFeeInfo.minEnergyPrice = null;
                    $that.editChargeRuleFeeInfo.maxEnergyPrice = null;
                }
                $('#editChargeRuleFeeModel').modal('show');
                vc.component.editChargeRuleFeeInfo.communityId = vc.getCurrentCommunity().communityId;
            });
        },
        methods: {
            editChargeRuleFeeValidate: function () {
                if ($that.editChargeRuleFeeInfo.chargeType === '1001') {
                    return vc.validate.validate({
                        editChargeRuleFeeInfo: vc.component.editChargeRuleFeeInfo
                    }, {
                        'editChargeRuleFeeInfo.ruleId': [
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
                        'editChargeRuleFeeInfo.minEnergyPrice': [
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
                        'editChargeRuleFeeInfo.maxEnergyPrice': [
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
                        'editChargeRuleFeeInfo.durationPrice': [
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
                        'editChargeRuleFeeInfo.remark': [
                            {
                                limit: "maxLength",
                                param: "512",
                                errInfo: "备注不能超过512"
                            },
                        ],
                        'editChargeRuleFeeInfo.crfId': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "编号不能为空"
                            }]
                    });
                } else if ($that.editChargeRuleFeeInfo.chargeType === '1002') {
                    return vc.validate.validate({
                        editChargeRuleFeeInfo: vc.component.editChargeRuleFeeInfo
                    }, {
                        'editChargeRuleFeeInfo.ruleId': [
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
                        'editChargeRuleFeeInfo.startTime': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "时段开始时间不能为空"
                            }
                        ],
                        'editChargeRuleFeeInfo.endTime': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "时段结束时间不能为空"
                            }
                        ],
                        'editChargeRuleFeeInfo.durationPrice': [
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
                        'editChargeRuleFeeInfo.remark': [
                            {
                                limit: "maxLength",
                                param: "512",
                                errInfo: "备注不能超过512"
                            },
                        ],
                        'editChargeRuleFeeInfo.crfId': [
                            {
                                limit: "required",
                                param: "",
                                errInfo: "编号不能为空"
                            }]
                    });
                }

            },
            editChargeRuleFee: function () {
                if (!vc.component.editChargeRuleFeeValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }

                vc.http.apiPost(
                    '/chargeRule.updateChargeRuleFee',
                    JSON.stringify(vc.component.editChargeRuleFeeInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editChargeRuleFeeModel').modal('hide');
                            vc.emit('chargeRuleFee', 'listChargeRuleFee', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditChargeRuleFeeInfo: function () {
                vc.component.editChargeRuleFeeInfo = {
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
            }
        }
    });

})(window.vc, window.vc.component);
