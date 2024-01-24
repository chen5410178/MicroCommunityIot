(function(vc) {

    vc.extends({
        data: {
            showAttendanceMachineLogInfo: {
                logId: '',
                reqParam: '',
                resParam: '',

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('showAttendanceMachineLog', 'openShowAttendanceMachineLogModal', function(_param) {
                vc.copyObject(_param, $that.showAttendanceMachineLogInfo);
                $that.loadShowAttendanceMachineLogInfo();
                $('#showAttendanceMachineLogModel').modal('show');
            });
        },
        methods: {

            loadShowAttendanceMachineLogInfo: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        logId: $that.showAttendanceMachineLogInfo.logId,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/attendance.listAttendanceMachineLog',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.showAttendanceMachineLogInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            clearShowAttendanceMachineLogInfo: function() {
                vc.component.showAttendanceMachineLogInfo = {
                    logId: '',
                    reqParam: '',
                    resParam: '',
    
                };
            }
        }
    });

})(window.vc);