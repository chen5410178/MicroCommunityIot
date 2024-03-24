/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailAccessControlInoutInfo: {
                inouts: [],
                roomId:'',
                ownerId:'',
                link:''
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailAccessControlInout', 'switch', function (_data) {
                $that.ownerDetailAccessControlInoutInfo.roomId = _data.roomId;
                $that.ownerDetailAccessControlInoutInfo.ownerId = _data.ownerId;
                $that.ownerDetailAccessControlInoutInfo.link = _data.link;
                
                $that._loadOwnerDetailAccessControlInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailAccessControlInout', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailAccessControlInoutData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailAccessControlInout', 'notify', function (_data) {
                $that._loadOwnerDetailAccessControlInoutData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailAccessControlInoutData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personId:$that.ownerDetailAccessControlInoutInfo.ownerId,
                        tel:$that.ownerDetailAccessControlInoutInfo.link,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/accessControlInout.listAccessControlInout',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailAccessControlInoutInfo.inouts = _roomInfo.data;
                        vc.emit('ownerDetailAccessControlInout', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailAccessControlInout: function () {
                $that._loadOwnerDetailAccessControlInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },

        }
    });
})(window.vc);