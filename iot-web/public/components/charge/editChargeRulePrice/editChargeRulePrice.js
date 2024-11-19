(function (vc, vm) {

    vc.extends({
        data: {
            editChargeRulePriceInfo: {
                crpId: '',
                ruleId: '',
                cpaId: '',
                price: '',
                servicePrice: '',
                remark: '',
                chargeType: '',
                startTime: '',
                endTime: ''
            },
            peakAlleyPriceEdit:{
                prices: [
                ],
            }
        },
        _initMethod: function () {
           
        },
        _initEvent: function () {
            vc.on('editChargeRulePrice', 'openEditChargeRulePriceModal', function (_params) {
                $that.refreshEditChargeRulePriceInfo();
                vc.copyObject(_params, $that.editChargeRulePriceInfo);
                vc.initHourMinute('editStartTime', function (_value) {
                    $that.editChargeRulePriceInfo.startTime = _value;
                });
                vc.initHourMinute('editEndTime', function (_value) {
                    $that.editChargeRulePriceInfo.endTime = _value;
                });
                $('#editChargeRulePriceModel').modal('show');
                $that.editChargeRulePriceInfo.communityId = vc.getCurrentCommunity().communityId;
                $that._listPeakAlleyPricesEdit();
            });
        },
        methods: {
            _listPeakAlleyPricesEdit: function() {
                let param = {
                    params: {page:1,row:4,communityId:vc.getCurrentCommunity().communityId,ruleId:$that.editChargeRulePriceInfo.ruleId}
                };
                //发送get请求
                vc.http.apiGet('/chargePeakAlleyPrice.listChargePeakAlleyPrice',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.peakAlleyPriceEdit.prices = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            editChargeRulePriceValidate: function () {
                return vc.validate.validate({
                    editChargeRulePriceInfo: $that.editChargeRulePriceInfo
                }, {
                    'editChargeRulePriceInfo.ruleId': [
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
                    'editChargeRulePriceInfo.startTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "时段开始时间不能为空"
                        }
                    ],
                    'editChargeRulePriceInfo.endTime': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "时段结束时间不能为空"
                        }
                    ],
                    // 'editChargeRulePriceInfo.price': [
                    //     {
                    //         limit: "required",
                    //         param: "",
                    //         errInfo: "小时电价不能为空"
                    //     },
                    //     {
                    //         limit: "maxLength",
                    //         param: "30",
                    //         errInfo: "小时电价不能超过30"
                    //     },
                    // ],
                    // 'editChargeRulePriceInfo.servicePrice': [
                    //     {
                    //         limit: "required",
                    //         param: "",
                    //         errInfo: "服务单价不能为空"
                    //     },
                    //     {
                    //         limit: "maxLength",
                    //         param: "30",
                    //         errInfo: "服务单价不能超过30"
                    //     },
                    // ],
                    'editChargeRulePriceInfo.remark': [
                        {
                            limit: "maxLength",
                            param: "512",
                            errInfo: "备注不能超过512"
                        },
                    ],
                    'editChargeRulePriceInfo.crpId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "编号不能为空"
                        }]
                });
            },
            editChargeRulePrice: function () {
                if (!$that.editChargeRulePriceValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.peakAlleyPriceEdit.prices.forEach((item, index) => {
                    if (item.cpaId == $that.editChargeRulePriceInfo.cpaId){
                        $that.editChargeRulePriceInfo.price = item.price;
                        $that.editChargeRulePriceInfo.servicePrice =  item.servicePrice;
                    }
                })
                vc.http.apiPost(
                    '/chargeRule.updateChargeRulePrice',
                    JSON.stringify($that.editChargeRulePriceInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#editChargeRulePriceModel').modal('hide');
                            vc.emit('chargeRulePrice', 'listChargeRulePrice', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            refreshEditChargeRulePriceInfo: function () {
                $that.editChargeRulePriceInfo = {
                    crpId: '',
                    cpaId: '',
                    ruleId: '',
                    price: '',
                    servicePrice: '',
                    remark: '',
                    chargeType: '',
                    startTime: '',
                    endTime: ''
                }
            }
        }
    });

})(window.vc, window.$that);
