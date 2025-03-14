/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminChargeOrderInfo: {
                adminChargeOrders: [],
                chargeTypes: [],
                total: 0,
                records: 1,
                moreCondition: false,
                orderId: '',
                conditions: {
                    orderId: '',
                    personName: '',
                    personTel: '',
                    machineName: '',
                    portName: '',
                    communityId:'',
                    state: '',
                    queryStartTime:'',
                    queryEndTime:'',
                    chargeType:''
                }
            }
        },
        _initMethod: function() {
            vc.on('selectAdminCommunity', 'changeCommunity', function (_community) {
                $that.adminChargeOrderInfo.conditions.communityId = _community.communityId;
                $that._listAdminChargeOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            $that._listAdminChargeOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.adminChargeOrderInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.adminChargeOrderInfo.conditions.queryEndTime = _value;
            });
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
                $that.adminChargeOrderInfo.chargeTypes = _chargeTypes;
            })
        },
        _initEvent: function() {

            vc.on('adminChargeOrder', 'listAdminChargeOrder', function(_param) {
                $that._listAdminChargeOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listAdminChargeOrders(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listAdminChargeOrders: function(_page, _rows) {

                $that.adminChargeOrderInfo.conditions.page = _page;
                $that.adminChargeOrderInfo.conditions.row = _rows;
                let param = {
                    params: $that.adminChargeOrderInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listAdminChargeMachineOrder',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.adminChargeOrderInfo.total = _json.total;
                        $that.adminChargeOrderInfo.records = _json.records;
                        $that.adminChargeOrderInfo.adminChargeOrders = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.adminChargeOrderInfo.records,
                            currentPage: _page,
                            dataCount:_json.total
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchChargeType:function(_item){
                $that.adminChargeOrderInfo.conditions.chargeType= _item.statusCd;
                $that._listAdminChargeOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _queryAdminChargeOrderMethod: function() {
                $that._listAdminChargeOrders(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _viewOrderAccts: function(_order) { // 展示扣款明细
                vc.jumpToPage('/#/pages/charge/adminChargeOrderAccts?orderId=' + _order.orderId)
            },
            _showStopCharge: function(_order) {
                vc.emit('stopChargeMachine', 'openStopChargeMachineModal', _order);
            },
            _showThrowStopCharge:function(_order){
                vc.emit('throwStopChargeMachine', 'openThrowStopChargeMachineModal', _order);
            },
            _viewAccount: function(_order) {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        acctId: _order.acctDetailId,
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                }
                vc.http.apiGet('/account.queryOwnerAccount',
                    param,
                    function(json, res) {
                        let _acct = JSON.parse(json).data[0];
                        let _data = {
                            "账户": _acct.acctName,
                            "账户类型": _acct.acctTypeName,
                            "账户余额": _acct.amount,
                            "账户编号": _acct.acctId,
                        };

                        vc.emit('viewData', 'openViewDataModal', {
                            title: _acct.acctName + " 详情",
                            data: _data
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );

            },
            _toChargeMachineDetail: function (_adminChargeOrder) {
                vc.jumpToPage('/#/pages/charge/chargeMachineDetail?machineId=' + _adminChargeOrder.machineId +
                    "&orderId=" + _adminChargeOrder.orderId + "&portId=" + _adminChargeOrder.portId + "&acctId=" +
                    _adminChargeOrder.acctDetailId
                );
            }
        }
    });
})(window.vc);