/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailLockPersonInfo: {
                lockPersons: [],
                ownerId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailLockPerson', 'switch', function (_data) {
                $that.ownerDetailLockPersonInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailLockPersonData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailLockPerson', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailLockPersonData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailLockPerson', 'notify', function (_data) {
                $that._loadOwnerDetailLockPersonData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailLockPersonData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personId:$that.ownerDetailLockPersonInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/lockPerson.listLockPerson',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailLockPersonInfo.lockPersons = _roomInfo.data;
                        vc.emit('ownerDetailLockPerson', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailLockPerson: function () {
                $that._loadOwnerDetailLockPersonData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);