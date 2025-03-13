/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            aAccessControlDetailFloorInfo: {
                machineId: '',
                floors: [],
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('aAccessControlDetailFloor', 'switch', function (_data) {
                $that.aAccessControlDetailFloorInfo.machineId = _data.machineId;
                $that._loadAAccessControlDetailFloor(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('aAccessControlDetailFloor', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAAccessControlDetailFloor(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAAccessControlDetailFloor: function (_page, _row) {
                let param = {
                    params: {
                        machineId: $that.aAccessControlDetailFloorInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlFloor.listAdminAccessControlFloor',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.aAccessControlDetailFloorInfo.floors = _json.data;
                        vc.emit('aAccessControlDetailFloor', 'paginationPlus', 'init', {
                            total: _json.records,
                            dataCount: _json.total,
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