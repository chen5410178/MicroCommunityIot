/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            aAccessControlDetailOrgInfo: {
                machineId: '',
                orgs: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('aAccessControlDetailOrg', 'switch', function (_data) {
                $that.aAccessControlDetailOrgInfo.machineId = _data.machineId;
                $that._loadAAccessControlDetailOrg(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('aAccessControlDetailOrg', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAAccessControlDetailOrg(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAAccessControlDetailOrg: function (_page, _row) {
                let param = {
                    params: {
                        machineId: $that.aAccessControlDetailOrgInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlOrg.listAdminAccessControlOrg',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.aAccessControlDetailOrgInfo.orgs = _accessControlInfo.data;
                        vc.emit('aAccessControlDetailOrg', 'paginationPlus', 'init', {
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