/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlDetailAccessControlInoutInfo: {
                machineId: '',
                accessControlInouts: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('accessControlDetailAccessControlInout', 'switch', function (_data) {
                $that.accessControlDetailAccessControlInoutInfo.machineId = _data.machineId;
                $that._loadAccessControlDetailAccessControlInout(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('accessControlDetailAccessControlInout', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAccessControlDetailAccessControlInout(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAccessControlDetailAccessControlInout: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.accessControlDetailAccessControlInoutInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlInout.listAccessControlInout',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.accessControlDetailAccessControlInoutInfo.accessControlInouts = _accessControlInfo.data;
                        vc.emit('accessControlDetailAccessControlInout', 'paginationPlus', 'init', {
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