/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminMeterInfo: {
                meterMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                meterTypes: [],
                factorys: [],
                conditions: {
                    machineNameLike: '',
                    address: '',
                    meterType: '',
                    machineModel: '',
                    roomNameLike: '',
                    implBean: '',
                    communityId: ''
                }
            }
        },
        _initMethod: function() {
            vc.on('selectAdminCommunity','changeCommunity',function(_community){
                $that.adminMeterInfo.conditions.communityId = _community.communityId;
                $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listFactorys();
        },
        _initEvent: function() {

            vc.on('adminMeter', 'listMeterMachine', function(_param) {
                $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listMeterMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterMachines: function(_page, _rows) {

                $that.adminMeterInfo.conditions.page = _page;
                $that.adminMeterInfo.conditions.row = _rows;
                let param = {
                    params: $that.adminMeterInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/meterMachine.listAdminMeterMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            $that.adminMeterInfo.total = _json.total;
                            $that.adminMeterInfo.records = _json.records;
                            $that.adminMeterInfo.meterMachines = _json.data;
                            vc.emit('pagination', 'init', {
                                total: $that.adminMeterInfo.records,
                                currentPage: _page
                            });
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _queryMeterMachineMethod: function() {
                $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.adminMeterInfo.moreCondition) {
                    $that.adminMeterInfo.moreCondition = false;
                } else {
                    $that.adminMeterInfo.moreCondition = true;
                }
            },
            _listFactorys: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 500,
                    }
                };
                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachineFactory', param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.adminMeterInfo.factorys = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _getMeterTypeName: function(meterType) {
                let _meterTypeName = "";
                $that.adminMeterInfo.meterTypes.forEach(item => {
                    if (meterType == item.typeId) {
                        _meterTypeName = item.typeName
                    }
                });
                if (!_meterTypeName) {
                    _meterTypeName = '-';
                }
                return _meterTypeName;
            },
            _toMeterDetail: function (_meterMachine) {
                vc.jumpToPage('/#/pages/meter/adminMeterDetail?machineId=' + _meterMachine.machineId + "&roomId=" + _meterMachine.roomId);
            },
        }
    });
})(window.vc);