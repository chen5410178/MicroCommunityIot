(function (vc, vm) {

    vc.extends({
        data: {
            deleteMachineCollectInfo: {

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteMachineCollect', 'openDeleteMachineCollectModal', function (_params) {

                $that.deleteMachineCollectInfo = _params;
                $('#deleteMachineCollectModel').modal('show');

            });
        },
        methods: {
            deleteMachineCollect: function () {
                vc.http.apiPost(
                    '/monitor.deleteMonitorCollect',
                    JSON.stringify($that.deleteMachineCollectInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteMachineCollectModel').modal('hide');
                            vc.emit('machineCollect', 'listMachineCollect', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteMachineCollectModel: function () {
                $('#deleteMachineCollectModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
