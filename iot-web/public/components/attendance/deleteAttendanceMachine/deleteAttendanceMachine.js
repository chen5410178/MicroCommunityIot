(function (vc, vm) {

    vc.extends({
        data: {
            deleteAttendanceMachineInfo: {

            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('deleteAttendanceMachine', 'openDeleteAttendanceMachineModal', function (_params) {
                $that.deleteAttendanceMachineInfo = _params;
                $('#deleteAttendanceMachineModel').modal('show');
            });
        },
        methods: {
            deleteAttendanceMachine: function () {
                $that.deleteAttendanceMachineInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/attendance.deleteAttendanceMachine',
                    JSON.stringify($that.deleteAttendanceMachineInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteAttendanceMachineModel').modal('hide');
                            vc.emit('attendanceMachine', 'listAttendanceMachine', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteAttendanceMachineModel: function () {
                $('#deleteAttendanceMachineModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);
