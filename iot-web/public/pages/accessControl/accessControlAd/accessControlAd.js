/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlAdInfo: {
                accessControlAds: [],
                total: 0,
                records: 1,
                moreCondition: false,
                adId: '',
                conditions: {
                    adName: '',
                    communityId: '',
                    state: '',

                }
            }
        },
        _initMethod: function () {
            $that._listAccessControlAds(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('accessControlAd', 'listAccessControlAd', function (_param) {
                $that._listAccessControlAds(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAccessControlAds(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccessControlAds: function (_page, _rows) {

                $that.accessControlAdInfo.conditions.page = _page;
                $that.accessControlAdInfo.conditions.row = _rows;
                $that.accessControlAdInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.accessControlAdInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/ad.listAccessControlAd',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.accessControlAdInfo.total = _json.total;
                        $that.accessControlAdInfo.records = _json.records;
                        $that.accessControlAdInfo.accessControlAds = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlAdInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddAccessControlAdModal: function () {
                vc.emit('addAccessControlAd', 'openAddAccessControlAdModal', {});
            },
            _openEditAccessControlAdModel: function (_accessControlAd) {
                vc.emit('editAccessControlAd', 'openEditAccessControlAdModal', _accessControlAd);
            },
            _openDeleteAccessControlAdModel: function (_accessControlAd) {
                vc.emit('deleteAccessControlAd', 'openDeleteAccessControlAdModal', _accessControlAd);
            },
            _queryAccessControlAdMethod: function () {
                $that._listAccessControlAds(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.accessControlAdInfo.moreCondition) {
                    $that.accessControlAdInfo.moreCondition = false;
                } else {
                    $that.accessControlAdInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
