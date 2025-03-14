/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminMeterRechargeInfo: {
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
                    detailType: "1001",
                    queryStartTime:'',
                    queryEndTime:'',
                    communityId:''
                }
            }
        },
        _initMethod: function () {
            vc.on('selectAdminCommunity', 'changeCommunity', function (_community) {
                $that.adminMeterRechargeInfo.conditions.communityId = _community.communityId;
                $that._listAdminMeterRecharges(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            $that._listAdminMeterRecharges(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.adminMeterRechargeInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.adminMeterRechargeInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listAdminMeterRecharges(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAdminMeterRecharges: function (_page, _rows) {

                $that.adminMeterRechargeInfo.conditions.page = _page;
                $that.adminMeterRechargeInfo.conditions.row = _rows;
                let param = {
                    params: $that.adminMeterRechargeInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/meterMachineCharge.listAdminMeterCharge',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.adminMeterRechargeInfo.total = _json.total;
                        $that.adminMeterRechargeInfo.records = _json.records;
                        $that.adminMeterRechargeInfo.details = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.adminMeterRechargeInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
         
            _queryAdminMeterRechargeMethod: function () {
                $that._listAdminMeterRecharges(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _resetAdminMeterRechargeMethod: function () {
                $that.adminMeterRechargeInfo.conditions = {
                    machineNameLike: "",
                    addressLike: "",
                    state: "",
                    roomNameLike: "",
                    communityId: "",
                    detailType: "1001",
                    queryStartTime:'',
                    queryEndTime:''
                }
                $that._listAdminMeterRecharges(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function () {
                if ($that.adminMeterRechargeInfo.moreCondition) {
                    $that.adminMeterRechargeInfo.moreCondition = false;
                } else {
                    $that.adminMeterRechargeInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
