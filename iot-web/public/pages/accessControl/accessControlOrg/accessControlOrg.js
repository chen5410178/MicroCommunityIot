/**
 入驻小区
 **/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROW = 10;
    vc.extends({
        data: {
            accessControlOrgInfo: {
                accessControls: [],
                total: 0,
                records: 0,
                moreCondition: false,
                orgName: '',
                conditions: {
                    orgId: '',
                    machineName: '',
                },
                currentPage: DEFAULT_PAGE,

            }
        },
        _initMethod: function() {


        },
        _initEvent: function() {
            vc.on('accessControlOrg', 'switchOrg', function(_param) {
                $that.accessControlOrgInfo.conditions.orgId = _param.orgId;
                $that.accessControlOrgInfo.orgName = _param.orgName;
                vc.component.listAccessControlOrg(DEFAULT_PAGE, DEFAULT_ROW);
            });
            vc.on('accessControlOrg', 'listAccessControlOrg', function(_param) {
                $that.listAccessControlOrg($that.accessControlOrgInfo.currentPage, DEFAULT_ROW);
            });
            vc.on('accessControlOrg', 'loadData', function(_param) {
                $that.listAccessControlOrg($that.accessControlOrgInfo.currentPage, DEFAULT_ROW);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that.accessControlOrgInfo.currentPage = _currentPage;
                $that.listAccessControlOrg(_currentPage, DEFAULT_ROW);
            });
        },
        methods: {
            listAccessControlOrg: function(_page, _row) {
                $that.accessControlOrgInfo.conditions.page = _page;
                $that.accessControlOrgInfo.conditions.row = _row;
                $that.accessControlOrgInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: JSON.parse(JSON.stringify($that.accessControlOrgInfo.conditions))
                };

                //发送get请求
                vc.http.apiGet('/accessControlOrg.listAccessControlOrg',
                    param,
                    function(json, res) {
                        let listAccessControlOrgData = JSON.parse(json);
                        $that.accessControlOrgInfo.total = listAccessControlOrgData.total;
                        $that.accessControlOrgInfo.records = listAccessControlOrgData.records;
                        $that.accessControlOrgInfo.accessControls = listAccessControlOrgData.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlOrgInfo.records,
                            dataCount: $that.accessControlOrgInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _openAuthorizeAccessControlModel: function(_accessControlOrg) {
                let _orgId = $that.accessControlOrgInfo.conditions.orgId;

                if (!_orgId) {
                    vc.toast('请选择左边需要授权的组织');
                    return;
                }

                vc.emit('authorizeAccessControl', 'openAddAccessControlModal', {
                    orgId: _orgId,
                });
            },
            _openDelAccessControlOrgModel: function(_accessControlOrg) {
                vc.emit('deleteAccessControlOrg', 'openDeleteAccessControlOrgModal', _accessControlOrg);
            },

            _queryAccessControlOrgMethod: function() {
                $that.listAccessControlOrg(DEFAULT_PAGE, DEFAULT_ROW);
            },


            _moreCondition: function() {
                if ($that.accessControlOrgInfo.moreCondition) {
                    $that.accessControlOrgInfo.moreCondition = false;
                } else {
                    $that.accessControlOrgInfo.moreCondition = true;
                }
            },

        }
    });
})(window.vc);