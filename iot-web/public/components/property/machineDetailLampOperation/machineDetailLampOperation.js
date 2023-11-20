/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailLampOperationInfo: {
                operations: [],
                machineId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailLampOperation', 'switch', function (_data) {
                $that.machineDetailLampOperationInfo.machineId = _data.machineId;
                $that._loadMachineDetailLampOperationData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailLampOperation', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailLampOperationData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailLampOperationData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        machineId:$that.machineDetailLampOperationInfo.machineId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/lampMachineOperation.listLampMachineOperation',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailLampOperationInfo.operations = _machineInfo.data;
                        vc.emit('machineDetailLampOperation', 'paginationPlus', 'init', {
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