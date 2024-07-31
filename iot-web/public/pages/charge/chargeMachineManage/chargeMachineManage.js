/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeMachineManageInfo: {
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
            vc.getDict('charge_rule','charge_type',function (_data) {
                let _chargeTypes = [
                    {
                        name:'全部',
                        statusCd:''
                    }
                ];
                _data.forEach(item => {
                    _chargeTypes.push(item);
                });
                $that.chargeMachineManageInfo.chargeTypes = _chargeTypes;
            })
            $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('chargeMachineManage', 'listChargeMachine', function(_param) {
                $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listChargeMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachines: function(_page, _rows) {

                $that.chargeMachineManageInfo.conditions.page = _page;
                $that.chargeMachineManageInfo.conditions.row = _rows;
                $that.chargeMachineManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                
                let param = {
                    params: $that.chargeMachineManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeMachineManageInfo.total = _json.total;
                        $that.chargeMachineManageInfo.records = _json.records;
                        $that.chargeMachineManageInfo.chargeMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeMachineManageInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddChargeMachineModal: function() {
                vc.emit('addChargeMachine', 'openAddChargeMachineModal', {});
            },
            _openEditChargeMachineModel: function(_chargeMachine) {
                vc.emit('editChargeMachine', 'openEditChargeMachineModal', _chargeMachine);
            },
            _openDeleteChargeMachineModel: function(_chargeMachine) {
                vc.emit('deleteChargeMachine', 'openDeleteChargeMachineModal', _chargeMachine);
            },
            _openRestartChargeMachineModel: function(_chargeMachine) {
                vc.emit('restartChargeMachine','openRestartChargeMachineModal', _chargeMachine);
            },
            _queryChargeMachineMethod: function() {
                $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.chargeMachineManageInfo.moreCondition) {
                    $that.chargeMachineManageInfo.moreCondition = false;
                } else {
                    $that.chargeMachineManageInfo.moreCondition = true;
                }
            },
            _viewPort: function(_chargeMachine) {
                vc.jumpToPage('/#/pages/charge/chargeMachinePortManage?machineId=' + _chargeMachine.machineId);
            },
            _chargeMachineQrCode: function(_chargeMachine) {
                vc.emit('chargeMachineQrCode', 'openChargeMachineQrCodeModal', _chargeMachine);
            },
            swatchChargeType:function(_item){
                $that.chargeMachineManageInfo.conditions.chargeType= _item.statusCd;
                $that._listChargeMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _toChargeMachineDetail: function (_chargeMachine) {
                vc.jumpToPage('/#/pages/charge/chargeMachineDetail?machineId=' + _chargeMachine.machineId);
            },
            _viewChargeMachineVideo:function(_chargeMachine){
                vc.emit('playMonitorVideo', 'openPlayMonitorVideoModal', {
                    machineId:_chargeMachine.monitorId,
                    machineName:_chargeMachine.monitorName,
                    communityId:_chargeMachine.communityId
                });
            }

        }
    });
})(window.vc);