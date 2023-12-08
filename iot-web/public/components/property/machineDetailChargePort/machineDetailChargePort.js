/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailChargePortInfo: {
                ports: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailChargePort', 'switch', function (_data) {
                $that.machineDetailChargePortInfo.machineId = _data.machineId;
                $that._loadMachineDetailChargePortData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailChargePort', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailChargePortData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailChargePortData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailChargePortInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachinePort',
                    param,
                    function (json) {
                        let _portInfo = JSON.parse(json);
                        $that.machineDetailChargePortInfo.ports = _portInfo.data;
                        vc.emit('machineDetailChargePort', 'paginationPlus', 'init', {
                            total: _portInfo.records,
                            dataCount: _portInfo.total,
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