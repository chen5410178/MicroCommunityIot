/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlDetailAccessControlLogInfo: {
                machineId: '',
                accessControlLogs: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('accessControlDetailAccessControlLog', 'switch', function (_data) {
                $that.accessControlDetailAccessControlLogInfo.machineId = _data.machineId;
                $that._loadAccessControlDetailAccessControlLog(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('accessControlDetailAccessControlLog', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAccessControlDetailAccessControlLog(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAccessControlDetailAccessControlLog: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.accessControlDetailAccessControlLogInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlLog.listAccessControlLog',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.accessControlDetailAccessControlLogInfo.accessControlLogs = _accessControlInfo.data;
                        vc.emit('accessControlDetailAccessControlLog', 'paginationPlus', 'init', {
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