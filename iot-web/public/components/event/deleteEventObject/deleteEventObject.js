(function (vc, vm) {

    vc.extends({
        data: {
            deleteEventObjectInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteEventObject', 'openDeleteEventObjectModal', function (_params) {
                $that.deleteEventObjectInfo = _params;
                $('#deleteEventObjectModel').modal('show');
            });
        },
        methods: {
            deleteEventObject: function () {

                vc.http.apiPost(
                    '/eventObject.deleteEventObject',
                    JSON.stringify($that.deleteEventObjectInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteEventObjectModel').modal('hide');
                            vc.emit('eventObjectManage', 'listEventObject', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteEventObjectModel: function () {
                $('#deleteEventObjectModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
