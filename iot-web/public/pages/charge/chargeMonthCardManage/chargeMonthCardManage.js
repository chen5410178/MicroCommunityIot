/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeMonthCardManageInfo: {
                chargeMonthCards: [],
                total: 0,
                records: 1,
                moreCondition: false,
                cardId: '',
                conditions: {
                    cardName: '',
                    cardMonth: '',
                    cardPrice: '',
                    communityId: ''
                }
            }
        },
        _initMethod: function() {
            $that._listChargeMonthCards(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('chargeMonthCardManage', 'listChargeMonthCard', function(_param) {
                $that._listChargeMonthCards(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listChargeMonthCards(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMonthCards: function(_page, _rows) {

                $that.chargeMonthCardManageInfo.conditions.page = _page;
                $that.chargeMonthCardManageInfo.conditions.row = _rows;
                $that.chargeMonthCardManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                
                let param = {
                    params: $that.chargeMonthCardManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeCard.listChargeMonthCard',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeMonthCardManageInfo.total = _json.total;
                        $that.chargeMonthCardManageInfo.records = _json.records;
                        $that.chargeMonthCardManageInfo.chargeMonthCards = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeMonthCardManageInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeMonthCardModal: function() {
                vc.emit('addChargeMonthCard', 'openAddChargeMonthCardModal', {});
            },
            _openEditChargeMonthCardModel: function(_chargeMonthCard) {
                vc.emit('editChargeMonthCard', 'openEditChargeMonthCardModal', _chargeMonthCard);
            },
            _openDeleteChargeMonthCardModel: function(_chargeMonthCard) {
                vc.emit('deleteChargeMonthCard', 'openDeleteChargeMonthCardModal', _chargeMonthCard);
            },
            _queryChargeMonthCardMethod: function() {
                $that._listChargeMonthCards(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.chargeMonthCardManageInfo.moreCondition) {
                    $that.chargeMonthCardManageInfo.moreCondition = false;
                } else {
                    $that.chargeMonthCardManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);