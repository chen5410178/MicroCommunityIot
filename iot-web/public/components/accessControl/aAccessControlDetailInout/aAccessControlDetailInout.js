/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            aAccessControlDetailInoutInfo: {
                machineId: '',
                inouts: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('aAccessControlDetailInout', 'switch', function (_data) {
                $that.aAccessControlDetailInoutInfo.machineId = _data.machineId;
                $that._loadAAccessControlDetailInout(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('aAccessControlDetailInout', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAAccessControlDetailInout(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAAccessControlDetailInout: function (_page, _row) {
                let param = {
                    params: {
                        machineId: $that.aAccessControlDetailInoutInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlInout.listAdminAccessControlInout',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.aAccessControlDetailInoutInfo.inouts = _accessControlInfo.data;
                        vc.emit('aAccessControlDetailInout', 'paginationPlus', 'init', {
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