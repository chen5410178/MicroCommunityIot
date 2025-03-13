/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminMeterDetailReadInfo: {
                reads: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminMeterDetailRead', 'switch', function (_data) {
                $that.adminMeterDetailReadInfo.machineId = _data.machineId;
                $that._loadAdminMeterDetailReadData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminMeterDetailRead', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminMeterDetailReadData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminMeterDetailReadData: function (_page, _row) {
                let param = {
                    params: {
                        machineId:$that.adminMeterDetailReadInfo.machineId,
                        detailType: '2002',
                        page:_page,
                        row:_row
                    }
                };
               
                //发送get请求
                vc.http.apiGet('/meterMachine.listAdminMeterReadRechargeDetail',
                    param,
                    function (json) {
                        let _readInfo = JSON.parse(json);
                        $that.adminMeterDetailReadInfo.reads = _readInfo.data;
                        vc.emit('adminMeterDetailRead', 'paginationPlus', 'init', {
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