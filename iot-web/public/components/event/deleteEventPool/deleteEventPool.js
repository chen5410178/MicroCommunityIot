(function (vc, vm) {

    vc.extends({
        data: {
            deleteEventPoolInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteEventPool', 'openDeleteEventPoolModal', function (_params) {
                $that.deleteEventPoolInfo = _params;
                $('#deleteEventPoolModel').modal('show');
            });
        },
        methods: {
            deleteEventPool: function () {
                vc.http.apiPost(
                    '/eventPool.deleteEventPool',
                    JSON.stringify($that.deleteEventPoolInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteEventPoolModel').modal('hide');
                            vc.emit('eventPoolManage', 'listEventPool', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteEventPoolModel: function () {
                $('#deleteEventPoolModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
