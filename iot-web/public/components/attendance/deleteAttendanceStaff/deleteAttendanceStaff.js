(function (vc, vm) {
    vc.extends({
        data: {
            deleteAttendanceStaffInfo: {}
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('deleteAttendanceStaff', 'openDeleteAttendanceStaffModal', function (_params) {
                $that.deleteAttendanceStaffInfo = _params;
                $('#deleteAttendanceStaffModel').modal('show');
            });
        },
        methods: {
            deleteAttendanceStaff: function () {
                $that.deleteAttendanceStaffInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/attendanceClasses.deleteAttendanceStaff',
                    JSON.stringify($that.deleteAttendanceStaffInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAttendanceStaffModel').modal('hide');
                            vc.emit('attendanceStaff', 'listAttendanceStaff', {});
                            vc.toast("删除成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(json);
                    });
            },
            closeDeleteAttendanceStaffModel: function () {
                $('#deleteAttendanceStaffModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);
