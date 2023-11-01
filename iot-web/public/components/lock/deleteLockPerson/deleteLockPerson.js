(function (vc, vm) {

    vc.extends({
        data: {
            deleteLockPersonInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteLockPerson', 'openDeleteLockPersonModal', function (_params) {
                $that.deleteLockPersonInfo = _params;
                $('#deleteLockPersonModel').modal('show');
            });
        },
        methods: {
            deleteLockPerson: function () {
                $that.deleteLockPersonInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/lockPerson.deleteLockPerson',
                    JSON.stringify($that.deleteLockPersonInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteLockPersonModel').modal('hide');
                            vc.emit('lockPersonManage', 'listLockPerson', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteLockPersonModel: function () {
                $('#deleteLockPersonModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
