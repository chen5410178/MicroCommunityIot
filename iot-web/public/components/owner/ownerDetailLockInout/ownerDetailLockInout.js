/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailLockInoutInfo: {
                inouts: [],
                ownerId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailLockInout', 'switch', function (_data) {
                $that.ownerDetailLockInoutInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailLockInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailLockInout', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailLockInoutData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailLockInout', 'notify', function (_data) {
                $that._loadOwnerDetailLockInoutData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailLockInoutData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personId:$that.ownerDetailLockInoutInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockInout.listLockInout',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailLockInoutInfo.inouts = _roomInfo.data;
                        vc.emit('ownerDetailLockInout', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailLockInout: function () {
                $that._loadOwnerDetailLockInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);