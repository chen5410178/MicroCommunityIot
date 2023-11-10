/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailAccessControlLogInfo: {
                logs: [],
                ownerId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailAccessControlLog', 'switch', function (_data) {
                $that.ownerDetailAccessControlLogInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailAccessControlLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailAccessControlLog', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailAccessControlLogData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailAccessControlLog', 'notify', function (_data) {
                $that._loadOwnerDetailAccessControlLogData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailAccessControlLogData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personId:$that.ownerDetailAccessControlLogInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/accessControlLog.listAccessControlLog',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailAccessControlLogInfo.logs = _roomInfo.data;
                        vc.emit('ownerDetailAccessControlLog', 'paginationPlus', 'init', {
                            total: _roomInfo.records,
                            dataCount: _roomInfo.total,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },
            //查询
            _qureyOwnerDetailAccessControlLog: function () {
                $that._loadOwnerDetailAccessControlLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openViewAccessControlLogModel: function(_log) {
                vc.emit('showAccessControlLog', 'openShowAccessControlLogModal', _log);
            },
        }
    });
})(window.vc);