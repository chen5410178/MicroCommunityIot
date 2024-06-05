(function (vc, vm) {

    vc.extends({
        data: {
            deleteTelTtsInfo: {

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteTelTts', 'openDeleteTelTtsModal', function (_params) {
                $that.deleteTelTtsInfo = _params;
                $('#deleteTelTtsModel').modal('show');
            });
        },
        methods: {
            deleteTelTts: function () {
                $that.deleteTelTtsInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/telTts.deleteTelTts',
                    JSON.stringify($that.deleteTelTtsInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteTelTtsModel').modal('hide');
                            vc.emit('telTts', 'listTelTts', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteTelTtsModel: function () {
                $('#deleteTelTtsModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
