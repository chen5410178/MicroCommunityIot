/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            accessControlDetailAccessControlFloorInfo: {
                machineId: '',
                accessControlFloors: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('accessControlDetailAccessControlFloor', 'switch', function (_data) {
                $that.accessControlDetailAccessControlFloorInfo.machineId = _data.machineId;
                $that._loadAccessControlDetailAccessControlFloor(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('accessControlDetailAccessControlFloor', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAccessControlDetailAccessControlFloor(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAccessControlDetailAccessControlFloor: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId: $that.accessControlDetailAccessControlFloorInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlFloor.listAccessControlFloor',
                    param,
                    function (json) {
                        let _accessControlInfo = JSON.parse(json);
                        $that.accessControlDetailAccessControlFloorInfo.accessControlFloors = _accessControlInfo.data;
                        vc.emit('accessControlDetailAccessControlFloor', 'paginationPlus', 'init', {
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