/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeDetailPortInfo: {
                ports: [],
                machineId:'',
                portId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('adminChargeDetailPort', 'switch', function (_data) {
                $that.adminChargeDetailPortInfo.machineId = _data.machineId;
                $that.adminChargeDetailPortInfo.portId = _data.portId;
                $that._loadAdminChargeDetailPortData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminChargeDetailPort', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadAdminChargeDetailPortData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadAdminChargeDetailPortData: function (_page, _row) {
                let param = {
                    params: {
                        machineId:$that.adminChargeDetailPortInfo.machineId,
                        portId:$that.adminChargeDetailPortInfo.portId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listAdminChargeMachinePort',
                    param,
                    function (json) {
                        let _portInfo = JSON.parse(json);
                        $that.adminChargeDetailPortInfo.ports = _portInfo.data;
                        vc.emit('adminChargeDetailPort', 'paginationPlus', 'init', {
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