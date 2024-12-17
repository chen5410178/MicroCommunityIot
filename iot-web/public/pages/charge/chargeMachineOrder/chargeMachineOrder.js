/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            chargeMachineOrderInfo: {
                chargeMachineOrders: [],
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

            $that._listChargeMachineOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('queryStartTime',function(_value){
                $that.chargeMachineOrderInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('queryEndTime',function(_value){
                $that.chargeMachineOrderInfo.conditions.queryEndTime = _value;
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
                $that.chargeMachineOrderInfo.chargeTypes = _chargeTypes;
            })
        },
        _initEvent: function() {

            vc.on('chargeMachineOrder', 'listChargeMachineOrder', function(_param) {
                $that._listChargeMachineOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listChargeMachineOrders(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listChargeMachineOrders: function(_page, _rows) {

                $that.chargeMachineOrderInfo.conditions.page = _page;
                $that.chargeMachineOrderInfo.conditions.row = _rows;
                $that.chargeMachineOrderInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.chargeMachineOrderInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/chargeMachine.listChargeMachineOrder',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.chargeMachineOrderInfo.total = _json.total;
                        $that.chargeMachineOrderInfo.records = _json.records;
                        $that.chargeMachineOrderInfo.chargeMachineOrders = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.chargeMachineOrderInfo.records,
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
                $that.chargeMachineOrderInfo.conditions.chargeType= _item.statusCd;
                $that._listChargeMachineOrders(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _queryChargeMachineOrderMethod: function() {
                $that._listChargeMachineOrders(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _viewOrderAccts: function(_order) { // 展示扣款明细
                vc.jumpToPage('/#/pages/charge/chargeMachineOrderAccts?orderId=' + _order.orderId)
            },
            _showStopCharge: function(_order) {
                vc.emit('stopChargeMachine', 'openStopChargeMachineModal', _order);
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
            _toChargeMachineDetail: function (_chargeMachineOrder) {
                vc.jumpToPage('/#/pages/charge/chargeMachineDetail?machineId=' + _chargeMachineOrder.machineId +
                    "&orderId=" + _chargeMachineOrder.orderId + "&portId=" + _chargeMachineOrder.portId + "&acctId=" +
                    _chargeMachineOrder.acctDetailId
                );
            }
        }
    });
})(window.vc);