/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            aAccessControlDetailLogInfo: {
                machineId: '',
                logs: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('aAccessControlDetailLog', 'switch', function (_data) {
                $that.aAccessControlDetailLogInfo.machineId = _data.machineId;
                $that._loadAAccessControlDetailLog(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('aAccessControlDetailLog', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAAccessControlDetailLog(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAAccessControlDetailLog: function (_page, _row) {
                let param = {
                    params: {
                        machineId: $that.aAccessControlDetailLogInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlLog.listAdminAccessControlLog',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.aAccessControlDetailLogInfo.logs = _accessControlInfo.data;
                        vc.emit('aAccessControlDetailLog', 'paginationPlus', 'init', {
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