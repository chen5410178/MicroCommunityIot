/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailMeterReadInfo: {
                reads: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailMeterRead', 'switch', function (_data) {
                $that.machineDetailMeterReadInfo.machineId = _data.machineId;
                $that._loadMachineDetailMeterReadData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailMeterRead', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailMeterReadData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailMeterReadData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailMeterReadInfo.machineId,
                        detailType: '2002',
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachineReadOrRechargeDetail',
                    param,
                    function (json) {
                        let _readInfo = JSON.parse(json);
                        $that.machineDetailMeterReadInfo.reads = _readInfo.data;
                        vc.emit('machineDetailMeterRead', 'paginationPlus', 'init', {
                            total: _readInfo.records,
                            dataCount: _readInfo.total,
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