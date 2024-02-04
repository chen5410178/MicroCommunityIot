/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeRuleFeeInfo: {
                chargeRuleFees: [],
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
            //$that._listChargeRuleFees(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {
            vc.on('chargeRuleFee', 'switch', function (_data) {
                $that.chargeRuleFeeInfo.conditions.ruleId = _data.ruleId;
                $that._listChargeRuleFees(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('chargeRuleFee', 'listChargeRuleFee', function (_param) {
                $that._listChargeRuleFees(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('chargeRuleFee', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._listChargeRuleFees(_currentPage, DEFAULT_ROWS);
                });
            vc.on('chargeType', 'switchChargeType', function (_param) {
                $that.chargeRuleFeeInfo.conditions.chargeType = _param.statusCd;
                $that._listChargeRuleFees(DEFAULT_PAGE, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeRuleFees: function (_page, _rows) {

                if(!$that.chargeRuleFeeInfo.conditions.ruleId){
                    return;
                }

                $that.chargeRuleFeeInfo.conditions.page = _page;
                $that.chargeRuleFeeInfo.conditions.row = _rows;
                $that.chargeRuleFeeInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                
                let param = {
                    params: $that.chargeRuleFeeInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeRule.listChargeRuleFee',
                    param,
                    function (json, res) {
                        var _chargeRuleFeeInfo = JSON.parse(json);
                        $that.chargeRuleFeeInfo.total = _chargeRuleFeeInfo.total;
                        $that.chargeRuleFeeInfo.records = _chargeRuleFeeInfo.records;
                        $that.chargeRuleFeeInfo.chargeRuleFees = _chargeRuleFeeInfo.data;
                        vc.emit('chargeRuleFee', 'paginationPlus', 'init', {
                            total: $that.chargeRuleFeeInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeRuleFeeModal: function () {
                if (!$that.chargeRuleFeeInfo.conditions.chargeType) {
                    vc.toast('未选择充电类型');
                    return;
                }
                vc.emit('addChargeRuleFee', 'openAddChargeRuleFeeModal', {
                    ruleId:$that.chargeRuleFeeInfo.conditions.ruleId,
                    chargeType:$that.chargeRuleFeeInfo.conditions.chargeType
                });
            },
            _openEditChargeRuleFeeModel: function (_chargeRuleFee) {
                vc.emit('editChargeRuleFee', 'openEditChargeRuleFeeModal', _chargeRuleFee);
            },
            _openDeleteChargeRuleFeeModel: function (_chargeRuleFee) {
                vc.emit('deleteChargeRuleFee', 'openDeleteChargeRuleFeeModal', _chargeRuleFee);
            },
            _queryChargeRuleFeeMethod: function () {
                if (!$that.chargeRuleFeeInfo.conditions.chargeType) {
                    vc.toast('未选择充电类型');
                    return;
                }
                $that._listChargeRuleFees(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.chargeRuleFeeInfo.moreCondition) {
                    $that.chargeRuleFeeInfo.moreCondition = false;
                } else {
                    $that.chargeRuleFeeInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);