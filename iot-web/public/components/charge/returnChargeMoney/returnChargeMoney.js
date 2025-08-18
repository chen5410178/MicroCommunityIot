(function (vc, vm) {

    vc.extends({
        data: {
            returnChargeMoneyInfo: {
                chargeMachineOrderAcctss: [],
                total: 0,
                records: 1,
                moreCondition: false,
                orderId: '',
                totalMoney: '',
                returnMoney: '',
                remark: '',
                communityId: vc.getCurrentCommunity().communityId,
                conditions: {
                    orderId: '',
                    personName: '',
                    personTel: '',
                    machineName: '',
                    portName: '',

                    state: '',
                }
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('returnChargeMoney', 'openReturnChargeMoneyModal', function (_params) {
                vc.component.refreshEditChargeRuleInfo();
                $('#returnChargeMoneyModel').modal('show');
                vc.copyObject(_params, vc.component.returnChargeMoneyInfo);
                let sum2 = 0;
                vc.component.returnChargeMoneyInfo.chargeMachineOrderAcctss.forEach(item => {
                    sum2 += Math.round(parseFloat(item.amount) * 100);
                });
                vc.component.returnChargeMoneyInfo.orderId = vc.component.returnChargeMoneyInfo.conditions.orderId;
                vc.component.returnChargeMoneyInfo.totalMoney = sum2 / 100;
            });
        },
        methods: {
            returnChargeMoneyValidate: function () {
                return vc.validate.validate({
                    returnChargeMoneyInfo: vc.component.returnChargeMoneyInfo
                }, {
                    'returnChargeMoneyInfo.returnMoney': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "退款金额不能为空"
                        },
                        {
                            limit: "maxLength",
                            param: "5",
                            errInfo: "退款金额不能超过5"
                        },
                    ],
                    'returnChargeMoneyInfo.remark': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "备注不能为空"
                        }, {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        }
                    ]
                });
            },
            validateMoneyEnhanced: function (totalMoney, returnMoney) {
                // 数值范围检查
                if (returnMoney <= 0) {
                    vc.toast("退款必须是正数");
                    return;
                }
                // 比较验证（考虑浮点精度）
                if (returnMoney - totalMoney >= Number.EPSILON) {
                    vc.toast("本次退款金额需要小于总扣款金额");
                    return;
                }
                return true;
            }
            ,
            returnChargeMoney: function () {
                if (!vc.component.returnChargeMoneyValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                if (!vc.component.validateMoneyEnhanced(vc.component.returnChargeMoneyInfo.totalMoney,vc.component.returnChargeMoneyInfo.returnMoney)) {
                    return;
                }

                vc.http.apiPost(
                    '/chargeMachine.orderRefundMoney',
                    JSON.stringify(vc.component.returnChargeMoneyInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#returnChargeMoneyModel').modal('hide');
                            vc.emit('chargeMachineOrderAccts', 'listChargeMachineOrderAccts', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            }
            ,
            refreshEditChargeRuleInfo: function () {
                vc.component.returnChargeMoneyInfo = {
                    chargeMachineOrderAcctss: [],
                    total: 0,
                    records: 1,
                    moreCondition: false,
                    orderId: '',
                    totalMoney: '',
                    returnMoney: '',
                    remark: '',
                    communityId: vc.getCurrentCommunity().communityId,
                    conditions: {
                        orderId: '',
                        personName: '',
                        personTel: '',
                        machineName: '',
                        portName: '',
                        state: '',
                    }
                }
            }
        }
    })
    ;

})(window.vc, window.vc.component);
