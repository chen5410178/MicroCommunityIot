/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeRulePriceInfo: {
                chargeRulePrices: [],
                total: 0,
                records: 1,
                moreCondition: false,
                textId: '',
                conditions: {
                    crcId: '',
                    ruleId: '',
                    communityId: '',
                    quantity: '',
                    chargeType: '',
                }
            }
        },
        _initMethod: function () {
            //$that._listChargeRulePrices(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.getDict('car_inout_payment', 'pay_type', function(_data) {
                $that.parkingAreaControlCustomCarInoutInfo.payTypes = _data;
            })
        },
        _initEvent: function () {
            vc.on('chargeRulePrice', 'switch', function (_data) {
                $that.chargeRulePriceInfo.conditions.ruleId = _data.ruleId;
                $that.chargeRulePriceInfo.conditions.chargeType = _data.chargeType;
                $that._listChargeRulePrices(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('chargeRulePrice', 'listChargeRulePrice', function (_param) {
                $that._listChargeRulePrices(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('chargeRulePrice', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._listChargeRulePrices(_currentPage, DEFAULT_ROWS);
                });
            // vc.on('chargeType', 'switchChargeType', function (_param) {
            //     $that.chargeRulePriceInfo.conditions.chargeType = _param.statusCd;
            //     $that._listChargeRulePrices(DEFAULT_PAGE, DEFAULT_ROWS);
            // });
        },
        methods: {
            _listChargeRulePrices: function (_page, _rows) {

                if(!$that.chargeRulePriceInfo.conditions.ruleId){
                    return;
                }

                $that.chargeRulePriceInfo.conditions.page = _page;
                $that.chargeRulePriceInfo.conditions.row = _rows;
                $that.chargeRulePriceInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                
                let param = {
                    params: $that.chargeRulePriceInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeRule.listChargeRulePrice',
                    param,
                    function (json, res) {
                        var _chargeRulePriceInfo = JSON.parse(json);
                        $that.chargeRulePriceInfo.total = _chargeRulePriceInfo.total;
                        $that.chargeRulePriceInfo.records = _chargeRulePriceInfo.records;
                        $that.chargeRulePriceInfo.chargeRulePrices = _chargeRulePriceInfo.data;
                        vc.emit('chargeRulePrice', 'paginationPlus', 'init', {
                            total: $that.chargeRulePriceInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargePeakAlleyPriceModal: function () {
                if (!$that.chargeRulePriceInfo.conditions.chargeType) {
                    vc.toast('未选择充电类型');
                    return;
                }
                vc.emit('addChargePeakAlleyPrice', 'openAddChargePeakAlleyPriceModal', {
                    ruleId:$that.chargeRulePriceInfo.conditions.ruleId,
                    chargeType:$that.chargeRulePriceInfo.conditions.chargeType
                });
            },
            _openAddChargeRulePriceModal: function () {
                if (!$that.chargeRulePriceInfo.conditions.chargeType) {
                    vc.toast('未选择充电类型');
                    return;
                }
                vc.emit('addChargeRulePrice', 'openAddChargeRulePriceModal', {
                    ruleId:$that.chargeRulePriceInfo.conditions.ruleId,
                    chargeType:$that.chargeRulePriceInfo.conditions.chargeType
                });
            },
            _openEditChargeRulePriceModel: function (_chargeRulePrice) {
                vc.emit('editChargeRulePrice', 'openEditChargeRulePriceModal', _chargeRulePrice);
            },
            _openDeleteChargeRulePriceModel: function (_chargeRulePrice) {
                vc.emit('deleteChargeRulePrice', 'openDeleteChargeRulePriceModal', _chargeRulePrice);
            },
            _queryChargeRulePriceMethod: function () {
                if (!$that.chargeRulePriceInfo.conditions.chargeType) {
                    vc.toast('未选择充电类型');
                    return;
                }
                $that._listChargeRulePrices(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.chargeRulePriceInfo.moreCondition) {
                    $that.chargeRulePriceInfo.moreCondition = false;
                } else {
                    $that.chargeRulePriceInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);