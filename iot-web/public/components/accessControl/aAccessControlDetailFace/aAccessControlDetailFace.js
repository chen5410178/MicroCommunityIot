/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            aAccessControlDetailFaceInfo: {
                machineId: '',
                faces: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('aAccessControlDetailFace', 'switch', function (_data) {
                $that.aAccessControlDetailFaceInfo.machineId = _data.machineId;
                $that._loadAAccessControlDetailFace(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('aAccessControlDetailFace', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAAccessControlDetailFace(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAAccessControlDetailFace: function (_page, _row) {
                let param = {
                    params: {
                        machineId: $that.aAccessControlDetailFaceInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlFace.listAdminAccessControlFace',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.aAccessControlDetailFaceInfo.faces = _accessControlInfo.data;
                        vc.emit('aAccessControlDetailFace', 'paginationPlus', 'init', {
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