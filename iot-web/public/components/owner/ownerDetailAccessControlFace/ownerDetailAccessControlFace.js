/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            ownerDetailAccessControlFaceInfo: {
                accessControlFaces: [],
                ownerId:'',
                roomNum: '',
                allOweFeeAmount:'0'
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('ownerDetailAccessControlFace', 'switch', function (_data) {
                $that.ownerDetailAccessControlFaceInfo.ownerId = _data.ownerId;
                $that._loadOwnerDetailAccessControlFaceData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('ownerDetailAccessControlFace', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._loadOwnerDetailAccessControlFaceData(_currentPage, DEFAULT_ROWS);
                });
            vc.on('ownerDetailAccessControlFace', 'notify', function (_data) {
                $that._loadOwnerDetailAccessControlFaceData(DEFAULT_PAGE,DEFAULT_ROWS);
            })
        },
        methods: {
            _loadOwnerDetailAccessControlFaceData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        personId:$that.ownerDetailAccessControlFaceInfo.ownerId,
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/accessControlFace.listAccessControlFace',
                    param,
                    function (json) {
                        let _roomInfo = JSON.parse(json);
                        $that.ownerDetailAccessControlFaceInfo.accessControlFaces = _roomInfo.data;
                        vc.emit('ownerDetailAccessControlFace', 'paginationPlus', 'init', {
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
            _qureyOwnerDetailAccessControlFace: function () {
                $that._loadOwnerDetailAccessControlFaceData(DEFAULT_PAGE, DEFAULT_ROWS);
            },
        }
    });
})(window.vc);