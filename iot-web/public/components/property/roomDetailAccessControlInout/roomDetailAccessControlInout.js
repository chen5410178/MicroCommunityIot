/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            roomDetailAccessControlInoutInfo: {
                inouts: [],
                roomId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('roomDetailAccessControlInout', 'switch', function (_data) {
                $that.roomDetailAccessControlInoutInfo.roomId = _data.roomId;
                $that._loadRoomDetailAccessControlInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('roomDetailAccessControlInout', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadRoomDetailAccessControlInoutData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('roomDetailAccessControlInout', 'notify', function (_data) {
                $that._loadRoomDetailAccessControlInoutData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadRoomDetailAccessControlInoutData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        roomId:$that.roomDetailAccessControlInoutInfo.roomId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/accessControlInout.listAccessControlInout',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.roomDetailAccessControlInoutInfo.inouts = _roomInfo.data;
                        vc.emit('roomDetailAccessControlInout', 'paginationPlus', 'init', {
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
            _qureyRoomDetailAccessControlInout: function () {
                $that._loadRoomDetailAccessControlInoutData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },

        }
    });
})(window.vc);