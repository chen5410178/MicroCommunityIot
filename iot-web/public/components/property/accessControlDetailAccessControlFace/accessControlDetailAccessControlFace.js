/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlDetailAccessControlFaceInfo: {
                machineId: '',
                accessControlFaces: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('accessControlDetailAccessControlFace', 'switch', function (_data) {
                $that.accessControlDetailAccessControlFaceInfo.machineId = _data.machineId;
                $that._loadAccessControlDetailAccessControlFace(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('accessControlDetailAccessControlFace', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAccessControlDetailAccessControlFace(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAccessControlDetailAccessControlFace: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.accessControlDetailAccessControlFaceInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlFace.listAccessControlFace',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.accessControlDetailAccessControlFaceInfo.accessControlFaces = _accessControlInfo.data;
                        vc.emit('accessControlDetailAccessControlFace', 'paginationPlus', 'init', {
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