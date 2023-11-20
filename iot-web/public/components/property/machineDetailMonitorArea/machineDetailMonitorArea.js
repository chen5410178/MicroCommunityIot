/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineDetailMonitorAreaInfo: {
                areas: [],
                maId:'',
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('machineDetailMonitorArea', 'switch', function (_data) {
                $that.machineDetailMonitorAreaInfo.maId = _data.maId;
                $that._loadMachineDetailMonitorAreaData(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('machineDetailMonitorArea', 'paginationPlus', 'page_event', function (_currentPage) {
                $that._loadMachineDetailMonitorAreaData(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _loadMachineDetailMonitorAreaData: function (_page, _row) {
                let param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                        maId:$that.machineDetailMonitorAreaInfo.maId,
                        page:_page,
                        row:_row
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitorArea.listMonitorArea',
                    param,
                    function (json) {
                        let _machineInfo = JSON.parse(json);
                        $that.machineDetailMonitorAreaInfo.areas = _machineInfo.data;
                        vc.emit('machineDetailMonitorArea', 'paginationPlus', 'init', {
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
            _openModel: function (_data, _title) {
                vc.emit('viewData', 'openEventViewDataModal', {
                    title: _title,
                    data: _data
                });
            },
        }
    });
})(window.vc);