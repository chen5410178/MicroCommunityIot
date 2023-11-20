/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailLiftReservationInfo: {
                reservations: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailLiftReservation', 'switch', function (_data) {
                $that.machineDetailLiftReservationInfo.machineId = _data.machineId;
                $that._loadMachineDetailLiftReservation(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailLiftReservation', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailLiftReservation(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailLiftReservation: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailLiftReservationInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/liftMachineReservation.listLiftMachineReservation',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailLiftReservationInfo.reservations = _machineInfo.data;
                        vc.emit('machineDetailLiftReservation', 'paginationPlus', 'init', {
                            total: _machineInfo.records,
                            dataCount: _machineInfo.total,
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