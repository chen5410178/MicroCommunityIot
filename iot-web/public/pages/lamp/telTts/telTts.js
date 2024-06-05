/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            telTtsInfo: {
                telTtss: [],
                total: 0,
                records: 1,
                moreCondition: false,
                ttId: '',
                conditions: {
                    ttsType: '',
                    ttsName: '',
                    communityId: '',

                }
            }
        },
        _initMethod: function () {
            $that._listTelTtss(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('telTts', 'listTelTts', function (_param) {
                $that._listTelTtss(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listTelTtss(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listTelTtss: function (_page, _rows) {

                $that.telTtsInfo.conditions.page = _page;
                $that.telTtsInfo.conditions.row = _rows;
                $that.telTtsInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.telTtsInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/telTts.listTelTts',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.telTtsInfo.total = _json.total;
                        $that.telTtsInfo.records = _json.records;
                        $that.telTtsInfo.telTtss = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.telTtsInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddTelTtsModal: function () {
                vc.emit('addTelTts', 'openAddTelTtsModal', {});
            },
            _openEditTelTtsModel: function (_telTts) {
                vc.emit('editTelTts', 'openEditTelTtsModal', _telTts);
            },
            _openDeleteTelTtsModel: function (_telTts) {
                vc.emit('deleteTelTts', 'openDeleteTelTtsModal', _telTts);
            },
            _queryTelTtsMethod: function () {
                $that._listTelTtss(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.telTtsInfo.moreCondition) {
                    $that.telTtsInfo.moreCondition = false;
                } else {
                    $that.telTtsInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
