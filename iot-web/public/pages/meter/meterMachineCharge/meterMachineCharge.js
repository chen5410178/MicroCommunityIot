/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterMachineChargeInfo: {
                details: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
               
            }
        },
        _initMethod: function () {
            $that.meterMachineChargeInfo.machineId = vc.getParam('machineId');
            vc.component._listMeterMachineCharges(DEFAULT_PAGE, DEFAULT_ROWS);
            
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listMeterMachineCharges(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterMachineCharges: function (_page, _rows) {

                let param = {
                    params: {
                        page:_page,
                        row:_rows,
                        machineId:$that.meterMachineChargeInfo.machineId,
                        communityId:vc.getCurrentCommunity().communityId,
                        detailType:'2002'
                    }
                };

                //发送get请求
                vc.http.apiGet('/meterMachineCharge.listMeterMachineCharge',
                    param,
                    function (json, res) {
                        var _meterMachineChargeInfo = JSON.parse(json);
                        vc.component.meterMachineChargeInfo.total = _meterMachineChargeInfo.total;
                        vc.component.meterMachineChargeInfo.records = _meterMachineChargeInfo.records;
                        vc.component.meterMachineChargeInfo.details = _meterMachineChargeInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.meterMachineChargeInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
         
            _queryMeterMachineChargeMethod: function () {
                vc.component._listMeterMachineCharges(DEFAULT_PAGE, DEFAULT_ROWS);

            },
        }
    });
})(window.vc);
