/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            visitDetailAccessControlInoutInfo: {
                inouts: [],
                tel:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('visitDetailAccessControlInout', 'switch', function (_data) {
                $that.visitDetailAccessControlInoutInfo.tel = _data.tel;
                $that._loadVisitDetailAccessControlInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('visitDetailAccessControlInout', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadVisitDetailAccessControlInoutData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadVisitDetailAccessControlInoutData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        tel:$that.visitDetailAccessControlInoutInfo.tel,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlInout.listAccessControlInout',
                    param,
                    function (json) {
                        let _inoutInfo = JSON.parse(json);
                        $that.visitDetailAccessControlInoutInfo.inouts = _inoutInfo.data;
                        vc.emit('visitDetailAccessControlInout', 'paginationPlus', 'init', {
                            total: _inoutInfo.records,
                            dataCount: _inoutInfo.total,
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