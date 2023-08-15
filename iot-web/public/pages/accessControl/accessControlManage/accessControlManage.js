/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlManageInfo: {
                accessControls: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineCode: '',
                    machineName: '',
                    locationName: '',

                }
            }
        },
        _initMethod: function() {
            $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('accessControlManage', 'listAccessControl', function(_param) {
                $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAccessControls(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAccessControls: function(_page, _rows) {

                $that.accessControlManageInfo.conditions.page = _page;
                $that.accessControlManageInfo.conditions.row = _rows;

                $that.accessControlManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.accessControlManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _accessControlManageInfo = JSON.parse(json);
                        $that.accessControlManageInfo.total = _accessControlManageInfo.total;
                        $that.accessControlManageInfo.records = _accessControlManageInfo.records;
                        $that.accessControlManageInfo.accessControls = _accessControlManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlManageInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddAccessControlModal: function() {
                vc.emit('addAccessControl', 'openAddAccessControlModal', {});
            },
            _openEditAccessControlModel: function(_accessControl) {
                vc.emit('editAccessControl', 'openEditAccessControlModal', _accessControl);
            },
            _openDeleteAccessControlModel: function(_accessControl) {
                vc.emit('deleteAccessControl', 'openDeleteAccessControlModal', _accessControl);
            },
            _queryAccessControlMethod: function() {
                $that._listAccessControls(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.accessControlManageInfo.moreCondition) {
                    $that.accessControlManageInfo.moreCondition = false;
                } else {
                    $that.accessControlManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);