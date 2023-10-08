(function (vc, vm) {

    vc.extends({
        data: {
            deleteEventStaffInfo: {}
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteEventStaff', 'openDeleteEventStaffModal', function (_params) {
                $that.deleteEventStaffInfo = _params;
                $('#deleteEventStaffModel').modal('show');
            });
        },
        methods: {
            deleteEventStaff: function () {
                $that.deleteEventStaffInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/eventStaff.deleteEventStaff',
                    JSON.stringify($that.deleteEventStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code === 0) {
                            //关闭model
                            $('#deleteEventStaffModel').modal('hide');
                            vc.emit('eventStaffManage', 'listEventStaff', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteEventStaffModel: function () {
                $('#deleteEventStaffModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
