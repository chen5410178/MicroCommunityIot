(function(vc, vm) {

    vc.extends({
        data: {
            throwStopChargeMachineInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('throwStopChargeMachine', 'openStopChargeMachineModal', function(_params) {

                $that.throwStopChargeMachineInfo = _params;
                $('#throwStopChargeMachineModel').modal('show');

            });
        },
        methods: {
            throwStopChargeMachine: function() {
                $that.throwStopChargeMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/chargeMachine.throwStopCharge',
                    JSON.stringify($that.throwStopChargeMachineInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#throwStopChargeMachineModel').modal('hide');
                            vc.emit('chargeMachineOrder', 'listChargeMachineOrder', {});
                            vc.emit('chargeMachinePortManage', 'listChargeMachinePort', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeThrowStopChargeMachineModel: function() {
                $('#throwStopChargeMachineModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);