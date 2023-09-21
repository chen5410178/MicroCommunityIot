/**
 入驻小区
 **/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            carBlackWhiteInfo: {
                carBlackWhites: [],
                total: 0,
                records: 1,
                moreCondition: false,
                carNum: '',
                conditions: {
                    blackWhite: '',
                    carNum: '',
                    bwId: ''
                },
                blackWhites: [{
                        name: '全部',
                        statusCd: ''
                    }, {
                        name: '白名单',
                        statusCd: '2222'
                    },
                    {
                        name: '黑名单',
                        statusCd: '1111'
                    }
                ]
            }
        },
        _initMethod: function() {
            $that._listCarBlackWhites(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {
            vc.on('carBlackWhite', 'listCarBlackWhite', function(_param) {
                $that._listCarBlackWhites(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listCarBlackWhites(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            swatchBlackWhiteType: function(_item) {
                $that.carBlackWhiteInfo.conditions.blackWhite = _item.statusCd;
                $that._listCarBlackWhites(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _listCarBlackWhites: function(_page, _rows) {
                $that.carBlackWhiteInfo.conditions.page = _page;
                $that.carBlackWhiteInfo.conditions.row = _rows;
                $that.carBlackWhiteInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.carBlackWhiteInfo.conditions
                };
                param.params.bwId = param.params.bwId.trim();
                param.params.carNum = param.params.carNum.trim();
                //发送get请求
                vc.http.apiGet('/carBlackWhite.listCarBlackWhites',
                    param,
                    function(json, res) {
                        var _carBlackWhiteInfo = JSON.parse(json);
                        $that.carBlackWhiteInfo.total = _carBlackWhiteInfo.total;
                        $that.carBlackWhiteInfo.records = _carBlackWhiteInfo.records;
                        $that.carBlackWhiteInfo.carBlackWhites = _carBlackWhiteInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.carBlackWhiteInfo.records,
                            dataCount: $that.carBlackWhiteInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddCarBlackWhiteModal: function() {
                vc.emit('addCarBlackWhite', 'openAddCarBlackWhiteModal', {});
            },
            _openEditCarBlackWhiteModel: function(_carBlackWhite) {
                vc.emit('editCarBlackWhite', 'openEditCarBlackWhiteModal', _carBlackWhite);
            },
            _openDeleteCarBlackWhiteModel: function(_carBlackWhite) {
                vc.emit('deleteCarBlackWhite', 'openDeleteCarBlackWhiteModal', _carBlackWhite);
            },
            //查询
            _queryCarBlackWhiteMethod: function() {
                $that._listCarBlackWhites(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetCarBlackWhiteMethod: function() {
                $that.carBlackWhiteInfo.conditions.bwId = "";
                $that.carBlackWhiteInfo.conditions.carNum = "";
                $that.carBlackWhiteInfo.conditions.blackWhite = "";
                $that._listCarBlackWhites(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function() {
                if ($that.carBlackWhiteInfo.moreCondition) {
                    $that.carBlackWhiteInfo.moreCondition = false;
                } else {
                    $that.carBlackWhiteInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);