(function (vc) {

    vc.extends({
        data: {
            chargeMeterInfo: {
                machineId: '',
                machineName: '',
                address: '',
                roomName: '',
                chargeMoney: '',

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('chargeMeter', 'openChargeMeterModal', function (_meterMachine) {
                vc.copyObject(_meterMachine,$that.chargeMeterInfo);
                $('#chargeMeterModel').modal('show');
            });
        },
        methods: {
            _settingRead: function () {

                $that.chargeMeterInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/meterMachineCharge.chargeMeterMoney',
                    JSON.stringify($that.chargeMeterInfo), {
                    emulateJSON: true
                },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#chargeMeterModel').modal('hide');
                            $that.clearChargeMeterInfo();
                            vc.emit('meterMachineManage', 'listMeterMachine', {});

                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');

                        vc.toast(errInfo);

                    });
            },
            clearChargeMeterInfo: function () {
                $that.chargeMeterInfo = {
                    machineId: '',
                    machineName: '',
                    address: '',
                    roomName: '',
                    chargeMoney: '',
                };
            },
        }
    });

})(window.vc);