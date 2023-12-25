/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlDetailAccessControlOrgInfo: {
                machineId: '',
                accessControlOrgs: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('accessControlDetailAccessControlOrg', 'switch', function (_data) {
                $that.accessControlDetailAccessControlOrgInfo.machineId = _data.machineId;
                $that._loadAccessControlDetailAccessControlOrg(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('accessControlDetailAccessControlOrg', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAccessControlDetailAccessControlOrg(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAccessControlDetailAccessControlOrg: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.accessControlDetailAccessControlOrgInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlOrg.listAccessControlOrg',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.accessControlDetailAccessControlOrgInfo.accessControlOrgs = _accessControlInfo.data;
                        vc.emit('accessControlDetailAccessControlOrg', 'paginationPlus', 'init', {
                            total: _accessControlInfo.records,
                            dataCount: _accessControlInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);