/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            meterMachineManageInfo: {
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
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function() {
            $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listMeterType();
            $that._listFactorys();
        },
        _initEvent: function() {

            vc.on('meterMachineManage', 'listMeterMachine', function(_param) {
                $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listMeterMachines(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMeterMachines: function(_page, _rows) {

                $that.meterMachineManageInfo.conditions.page = _page;
                $that.meterMachineManageInfo.conditions.row = _rows;
                let param = {
                    params: $that.meterMachineManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/meterMachine.listMeterMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.meterMachineManageInfo.total = _json.total;
                        $that.meterMachineManageInfo.records = _json.records;
                        $that.meterMachineManageInfo.meterMachines = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.meterMachineManageInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMeterMachineModal: function() {
                vc.jumpToPage('/#/pages/meter/addMeterMachine')
            },
            _openEditMeterMachineModel: function(_meterMachine) {
                vc.jumpToPage('/#/pages/meter/editMeterMachine?machineId=' + _meterMachine.machineId)
            },
            _openDeleteMeterMachineModel: function(_meterMachine) {
                vc.emit('deleteMeterMachine', 'openDeleteMeterMachineModal', _meterMachine);
            },
            _openSettingMeterMachineRead: function() {
                vc.emit('settingMeterMachineRead', 'openSettingMeterMachineReadModal', {});
            },
            _openCustomRead: function() {
                vc.emit('customReadMeterMachine', 'openCustomReadMeterMachineModal', {});
            },
            _openImportMeterMachine(){
                vc.emit('importMeterMachine', 'openImportMeterMachineModal',{})
            },
            _toChargeMoney:function(_meterMachine){
                 vc.emit('chargeMeter', 'openChargeMeterModal',_meterMachine);
            },

            _toDetail: function(_meterMachine) {
                vc.jumpToPage('/#/pages/meter/meterMachineDetail?machineId=' + _meterMachine.machineId)
            },
            _queryMeterMachineMethod: function() {
                $that._listMeterMachines(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.meterMachineManageInfo.moreCondition) {
                    $that.meterMachineManageInfo.moreCondition = false;
                } else {
                    $that.meterMachineManageInfo.moreCondition = true;
                }
            },
            _listMeterType: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 500,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/meterType.listMeterType',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.meterMachineManageInfo.meterTypes = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
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
                        let _feeConfigManageInfo = JSON.parse(json);
                        $that.meterMachineManageInfo.factorys = _feeConfigManageInfo.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _getMeterTypeName: function(meterType) {
                let _meterTypeName = "";
                $that.meterMachineManageInfo.meterTypes.forEach(item => {
                    if (meterType == item.typeId) {
                        _meterTypeName = item.typeName
                    }
                });
                if (!_meterTypeName) {
                    _meterTypeName = '-';
                }
                return _meterTypeName;
            },
            _controlSwitch: function (_meterMachine, _type) {
                _meterMachine.type = _type;
                vc.http.apiPost(
                    '/meterMachine.meterMachineControlSwitch',
                    JSON.stringify(_meterMachine), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            vc.toast('开闸/关闸请求已发送，表反馈速度可能有延时，请您刷新查看！');
                            vc.emit('meterMachineManage', 'listMeterMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            }


        }
    });
})(window.vc);