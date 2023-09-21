(function (vc, vm) {

    vc.extends({
        data: {
            deleteLampMachineFactoryInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLampMachineFactory', 'openDeleteLampMachineFactoryModal', function (_params) {

                $that.deleteLampMachineFactoryInfo = _params;
                $('#deleteLampMachineFactoryModel').modal('show');

            });
        },
        methods: {
            deleteLampMachineFactory: function () {
                $that.deleteLampMachineFactoryInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/lampMachineFactory.deleteLampMachineFactory',
                    JSON.stringify($that.deleteLampMachineFactoryInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLampMachineFactoryModel').modal('hide');
                            vc.emit('lampMachineFactoryManage', 'listLampMachineFactory', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteLampMachineFactoryModel: function () {
                $('#deleteLampMachineFactoryModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
