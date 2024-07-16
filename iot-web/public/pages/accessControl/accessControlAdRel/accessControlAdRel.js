/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROW = 10;
    vc.extends({
        data: {
            accessControlAdRelInfo: {
                accessControls: [],
                ads: [],
                total: 0,
                records: 0,
                moreCondition: false,
                orgName: '',
                conditions: {
                    orgId: '',
                    machineName: '',
                    adId: '',
                },
                currentPage: DEFAULT_PAGE,

            }
        },
        _initMethod: function () {
            $that._listAccessControlAds();

        },
        _initEvent: function () {
            vc.on('accessControlAdRel', 'switchOrg', function (_param) {
                $that.accessControlAdRelInfo.conditions.orgId = _param.orgId;
                $that.accessControlAdRelInfo.orgName = _param.orgName;
                vc.component.listAccessControlAdRel(DEFAULT_PAGE, DEFAULT_ROW);
            });
            vc.on('accessControlAdRel', 'listAccessControlAdRel', function (_param) {
                $that.listAccessControlAdRel($that.accessControlAdRelInfo.currentPage, DEFAULT_ROW);
            });
            vc.on('accessControlAdRel', 'loadData', function (_param) {
                $that.listAccessControlAdRel($that.accessControlAdRelInfo.currentPage, DEFAULT_ROW);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that.accessControlAdRelInfo.currentPage = _currentPage;
                $that.listAccessControlAdRel(_currentPage, DEFAULT_ROW);
            });
        },
        methods: {
            _listAccessControlAds: function () {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };

                //发送get请求
                vc.http.apiGet('/ad.listAccessControlAd',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.accessControlAdRelInfo.ads = _json.data;
                        if (_json.data && _json.data.length > 0) {
                            $that._swatchAd(_json.data[0]);
                        }
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _swatchAd: function (_ad) {
                $that.accessControlAdRelInfo.conditions.adId = _ad.adId;
                $that.listAccessControlAdRel(DEFAULT_PAGE, DEFAULT_ROW);
            },
            listAccessControlAdRel: function (_page, _row) {
                $that.accessControlAdRelInfo.conditions.page = _page;
                $that.accessControlAdRelInfo.conditions.row = _row;
                $that.accessControlAdRelInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: JSON.parse(JSON.stringify($that.accessControlAdRelInfo.conditions))
                };

                //发送get请求
                vc.http.apiGet('/ad.listAccessControlAdRel',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.accessControlAdRelInfo.total = _json.total;
                        $that.accessControlAdRelInfo.records = _json.records;
                        $that.accessControlAdRelInfo.accessControls = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlAdRelInfo.records,
                            dataCount: $that.accessControlAdRelInfo.total,
                            currentPage: _page
                        });
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _openAuthorizeAccessControlModel: function (_accessControlAdRel) {
                let _adId = $that.accessControlAdRelInfo.conditions.adId;

                if (!_adId) {
                    vc.toast('请选择左边广告');
                    return;
                }

                vc.emit('authorizeAccessControl', 'openAddAccessControlModal', {
                    adId: _adId,
                });
            },
            _openDelAccessControlAdRelModel: function (_accessControlAdRel) {
                vc.emit('deleteAccessControlAdRel', 'openDeleteAccessControlAdRelModal', _accessControlAdRel);
            },

            _queryAccessControlAdRelMethod: function () {
                $that.listAccessControlAdRel(DEFAULT_PAGE, DEFAULT_ROW);
            },


            _moreCondition: function () {
                if ($that.accessControlAdRelInfo.moreCondition) {
                    $that.accessControlAdRelInfo.moreCondition = false;
                } else {
                    $that.accessControlAdRelInfo.moreCondition = true;
                }
            },

        }
    });
})(window.vc);