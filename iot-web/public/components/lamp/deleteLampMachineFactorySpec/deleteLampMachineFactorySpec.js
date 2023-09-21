(function (vc, vm) {

    vc.extends({
        data: {
            deleteLampMachineFactorySpecInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLampMachineFactorySpec', 'openDeleteLampMachineFactorySpecModal', function (_params) {

                $that.deleteLampMachineFactorySpecInfo = _params;
                $('#deleteLampMachineFactorySpecModel').modal('show');

            });
        },
        methods: {
            deleteLampMachineFactorySpec: function () {
                $that.deleteLampMachineFactorySpecInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/lampMachineFactorySpec.deleteLampMachineFactorySpec',
                    JSON.stringify($that.deleteLampMachineFactorySpecInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLampMachineFactorySpecModel').modal('hide');
                            vc.emit('lampMachineFactorySpecManage', 'listLampMachineFactorySpec', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteLampMachineFactorySpecModel: function () {
                $('#deleteLampMachineFactorySpecModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
