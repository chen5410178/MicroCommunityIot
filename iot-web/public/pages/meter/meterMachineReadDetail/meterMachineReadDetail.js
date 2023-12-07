/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterMachineDetailInfo: {
                details: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineNameLike: "",
                    addressLike: "",
                    state: "",
                    roomNameLike: "",
                    communityId: "",
                    detailType: "2002"
                }
            }
        },
        _initMethod: function () {
            $that._listMeterMachineDetails(DEFAULT_PAGE, DEFAULT_ROWS);
            
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMeterMachineDetails(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterMachineDetails: function (_page, _rows) {

                $that.meterMachineDetailInfo.conditions.page = _page;
                $that.meterMachineDetailInfo.conditions.row = _rows;
                $that.meterMachineDetailInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.meterMachineDetailInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachineReadOrRechargeDetail',
                    param,
                    function (json, res) {
                        var _meterMachineDetailInfo = JSON.parse(json);
                        $that.meterMachineDetailInfo.total = _meterMachineDetailInfo.total;
                        $that.meterMachineDetailInfo.records = _meterMachineDetailInfo.records;
                        $that.meterMachineDetailInfo.details = _meterMachineDetailInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.meterMachineDetailInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
         
            _queryMeterMachineDetailMethod: function () {
                $that._listMeterMachineDetails(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetMeterMachineDetailMethod: function () {
                $that.meterMachineDetailInfo.conditions = {
                    machineNameLike: "",
                    addressLike: "",
                    state: "",
                    roomNameLike: "",
                    communityId: "",
                    detailType: "2002"
                }
                $that._listMeterMachineDetails(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.meterMachineDetailInfo.moreCondition) {
                    $that.meterMachineDetailInfo.moreCondition = false;
                } else {
                    $that.meterMachineDetailInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
