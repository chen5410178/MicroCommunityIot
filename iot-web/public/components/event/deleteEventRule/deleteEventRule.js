(function (vc, vm) {

    vc.extends({
        data: {
            deleteEventRuleInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteEventRule', 'openDeleteEventRuleModal', function (_params) {
                $that.deleteEventRuleInfo = _params;
                $('#deleteEventRuleModel').modal('show');
            });
        },
        methods: {
            deleteEventRule: function () {
                vc.http.apiPost(
                    '/eventRule.deleteEventRule',
                    JSON.stringify($that.deleteEventRuleInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteEventRuleModel').modal('hide');
                            vc.emit('eventRuleManage', 'listEventRule', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteEventRuleModel: function () {
                $('#deleteEventRuleModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
