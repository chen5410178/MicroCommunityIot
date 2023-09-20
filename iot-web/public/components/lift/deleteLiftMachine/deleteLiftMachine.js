(function (vc, vm) {

    vc.extends({
        data: {
            deleteLiftMachineInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLiftMachine', 'openDeleteLiftMachineModal', function (_params) {

                vc.component.deleteLiftMachineInfo = _params;
                $('#deleteLiftMachineModel').modal('show');

            });
        },
        methods: {
            deleteLiftMachine: function () {
                vc.component.deleteLiftMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'liftMachine.deleteLiftMachine',
                    JSON.stringify(vc.component.deleteLiftMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteLiftMachineModel').modal('hide');
                            vc.emit('liftMachineManage', 'listLiftMachine', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(json);

                    });
            },
            closeDeleteLiftMachineModel: function () {
                $('#deleteLiftMachineModel').modal('hide');
            }
        }
    });

})(window.vc, window.vc.component);
