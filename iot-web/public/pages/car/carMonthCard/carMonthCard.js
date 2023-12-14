/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            carMonthCardInfo: {
                carMonthCards: [],
                total: 0,
                records: 1,
                moreCondition: false,
                cardId: '',
                conditions: {
                    cardId: '',
                    cardName: '',
                    cardMonth: '',

                }
            }
        },
        _initMethod: function () {
            $that._listCarMonthCards(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('carMonthCard', 'listCarMonthCard', function (_param) {
                $that._listCarMonthCards(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listCarMonthCards(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCarMonthCards: function (_page, _rows) {

                $that.carMonthCardInfo.conditions.page = _page;
                $that.carMonthCardInfo.conditions.row = _rows;
                $that.carMonthCardInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.carMonthCardInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/carMonth.listCarMonthCard',
                    param,
                    function (json, res) {
                        var _carMonthCardInfo = JSON.parse(json);
                        $that.carMonthCardInfo.total = _carMonthCardInfo.total;
                        $that.carMonthCardInfo.records = _carMonthCardInfo.records;
                        $that.carMonthCardInfo.carMonthCards = _carMonthCardInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.carMonthCardInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddCarMonthCardModal: function () {
                vc.emit('addCarMonthCard', 'openAddCarMonthCardModal', {});
            },
            _openEditCarMonthCardModel: function (_carMonthCard) {
                vc.emit('editCarMonthCard', 'openEditCarMonthCardModal', _carMonthCard);
            },
            _openDeleteCarMonthCardModel: function (_carMonthCard) {
                vc.emit('deleteCarMonthCard', 'openDeleteCarMonthCardModal', _carMonthCard);
            },
            _queryCarMonthCardMethod: function () {
                $that._listCarMonthCards(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.carMonthCardInfo.moreCondition) {
                    $that.carMonthCardInfo.moreCondition = false;
                } else {
                    $that.carMonthCardInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
