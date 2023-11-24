/**
 入驻小区
 **/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            communityManageInfo: {
                communitys: [],
                total: 0,
                records: 1,
                storeTypeCd: vc.getData('/nav/getUserInfo').storeTypeCd,
                conditions: {
                    name: '',
                    cityCode: '',
                    communityId: ''
                },
                listColumns: []
            }
        },
        _initMethod: function() {
            $that._getColumns(function() {
                $that._listCommunitys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
        },
        _initEvent: function() {
            vc.on('communityManage', 'listCommunity', function(_param) {
                $that._listCommunitys(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on("communityManage", "communityManage", "notifyArea", function(_param) {
                $that.communityManageInfo.conditions.cityCode = _param.selectArea;
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listCommunitys(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCommunitys: function(_page, _rows) {
                $that.communityManageInfo.conditions.page = _page;
                $that.communityManageInfo.conditions.row = _rows;
                let _param = {
                        params: $that.communityManageInfo.conditions
                    }
                    //发送get请求
                vc.http.apiGet('/community.listCommunitys',
                    _param,
                    function(json, res) {
                        var _communityManageInfo = JSON.parse(json);
                        $that.communityManageInfo.total = _communityManageInfo.total;
                        $that.communityManageInfo.records = _communityManageInfo.records;
                        $that.communityManageInfo.communitys = _communityManageInfo.data;
                        $that.dealCommunityAttr(_communityManageInfo.communitys);
                        vc.emit('pagination', 'init', {
                            total: $that.communityManageInfo.records,
                            dataCount: $that.communityManageInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddCommunityModal: function() {
                vc.emit('addCommunity', 'openAddCommunityModal', {});
            },
            _openEditCommunityModel: function(_community) {
                vc.emit('editCommunity', 'openEditCommunityModal', _community);
            },
            _openDeleteCommunityModel: function(_community) {
                vc.emit('deleteCommunity', 'openDeleteCommunityModal', _community);
            },
            _openRecallCommunityModel: function(_community) {
                vc.emit('recallAuditFinishCommunity', 'openRecallAuditFinishCommunityModal', _community);
            },
            _queryCommunityMethod: function() {
                $that._listCommunitys(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openDeleteCommunityModel(_community) {
                vc.emit('deleteCommunity', 'openDeleteCommunityModal', _community);
            },
            dealCommunityAttr: function(communitys) {
                communitys.forEach(item => {
                    $that._getColumnsValue(item);
                });
            },
            _getColumnsValue: function(_community) {
                _community.listValues = [];
                if (!_community.hasOwnProperty('communityAttrDtos') || _community.communityAttrDtos.length < 1) {
                    $that.communityManageInfo.listColumns.forEach(_value => {
                        _community.listValues.push('');
                    })
                    return;
                }
                let _communityAttrDtos = _community.communityAttrDtos;
                $that.communityManageInfo.listColumns.forEach(_value => {
                    let _tmpValue = '';
                    _communityAttrDtos.forEach(_attrItem => {
                        if (_value.specCd == _attrItem.specCd) {
                            _tmpValue = _attrItem.value;
                        }
                    })
                    _community.listValues.push(_tmpValue);
                })
            },
            _getColumns: function(_call) {
                console.log('_getColumns');
                $that.communityManageInfo.listColumns = [];
                vc.getAttrSpec('building_community_attr', function(data) {
                    $that.communityManageInfo.listColumns = [];
                    data.forEach(item => {
                        if (item.listShow == 'Y') {
                            $that.communityManageInfo.listColumns.push({
                                specCd: item.specCd,
                                specName: item.specName
                            });
                        }
                    });
                    _call();
                });
            }
        }
    });
})(window.vc);