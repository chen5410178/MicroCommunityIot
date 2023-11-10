/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailLockLogInfo: {
                logs: [],
                ownerId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailLockLog', 'switch', function (_data) {
                $that.ownerDetailLockLogInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailLockLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailLockLog', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailLockLogData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailLockLog', 'notify', function (_data) {
                $that._loadOwnerDetailLockLogData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailLockLogData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personId:$that.ownerDetailLockLogInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockLog.listLockLog',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailLockLogInfo.logs = _roomInfo.data;
                        vc.emit('ownerDetailLockLog', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailLockLog: function () {
                $that._loadOwnerDetailLockLogData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openViewLockLogModel: function (_log) {
                vc.emit('showLockLog', 'openShowLockLogModal', _log);
            },
        }
    });
})(window.vc);