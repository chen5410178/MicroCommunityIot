/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeMachineInfo: {
                chargeMachines: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineName: '',
                    machineCode: '',
                    implBean: '',
                    communityId: '',
                    chargeType: ''
                },
                chargeTypes: []
            }
        },
        _initMethod: function() {
            vc.on('selectAdminCommunity','changeCommunity',function(_community){
                $that.adminChargeMachineInfo.conditions.communityId = _community.communityId;
                $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            vc.getDict('charge_machine','charge_type',function (_data) {
                let _chargeTypes = [
                    {
                        name:'全部',
                        statusCd:''
                    }
                ];
                _data.forEach(item => {
                    _chargeTypes.push(item);
                });
                $that.adminChargeMachineInfo.chargeTypes = _chargeTypes;
            })
            $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('adminChargeMachine', 'listChargeMachine', function(_param) {
                $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listChargeMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachines: function(_page, _rows) {

                $that.adminChargeMachineInfo.conditions.page = _page;
                $that.adminChargeMachineInfo.conditions.row = _rows;
                
                let param = {
                    params: $that.adminChargeMachineInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listAdminChargeMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.adminChargeMachineInfo.total = _json.total;
                        $that.adminChargeMachineInfo.records = _json.records;
                        $that.adminChargeMachineInfo.chargeMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.adminChargeMachineInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            
            _queryChargeMachineMethod: function() {
                $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.adminChargeMachineInfo.moreCondition) {
                    $that.adminChargeMachineInfo.moreCondition = false;
                } else {
                    $that.adminChargeMachineInfo.moreCondition = true;
                }
            },
            _toChargeMachineDetail: function (_chargeMachine) {
                vc.jumpToPage('/#/pages/charge/adminChargeDetail?machineId=' + _chargeMachine.machineId);
            },

        }
    });
})(window.vc);