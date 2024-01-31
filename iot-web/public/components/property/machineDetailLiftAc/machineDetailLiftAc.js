/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailLiftAcInfo: {
                reservations: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailLiftAc', 'switch', function (_data) {
                $that.machineDetailLiftAcInfo.machineId = _data.machineId;
                $that._loadMachineDetailLiftAc(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailLiftAc', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailLiftAc(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailLiftAc: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailLiftAcInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/liftMachineAc.listLiftMachineAc',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.machineDetailLiftAcInfo.reservations = _json.data;
                        vc.emit('machineDetailLiftAc', 'paginationPlus', 'init', {
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