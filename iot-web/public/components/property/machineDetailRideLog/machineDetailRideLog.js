/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailRideLogInfo: {
                liftRideLogs: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailRideLog', 'switch', function (_data) {
                $that.machineDetailRideLogInfo.machineId = _data.machineId;
                $that._loadMachineDetailRideLog(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailRideLog', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailRideLog(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailRideLog: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailRideLogInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/liftMachineLog.listLiftRideLog',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.machineDetailRideLogInfo.liftRideLogs = _json.data;
                        vc.emit('machineDetailRideLog', 'paginationPlus', 'init', {
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