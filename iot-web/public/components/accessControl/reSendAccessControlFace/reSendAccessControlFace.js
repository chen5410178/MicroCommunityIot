(function(vc, vm) {

    vc.extends({
        data: {
            reSendAccessControlFaceInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('reSendAccessControlFace', 'openDeleteAccessControlModal', function(_params) {

                $that.reSendAccessControlFaceInfo = _params;
                $('#reSendAccessControlFaceModel').modal('show');

            });
        },
        methods: {
            reSendAccessControlFace: function() {
                $that.reSendAccessControlFaceInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/accessControl.reSendAccessControlFace',
                    JSON.stringify($that.reSendAccessControlFaceInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#reSendAccessControlFaceModel').modal('hide');
                            vc.emit('accessControlManage', 'listAccessControl', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteAccessControlModel: function() {
                $('#reSendAccessControlFaceModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);