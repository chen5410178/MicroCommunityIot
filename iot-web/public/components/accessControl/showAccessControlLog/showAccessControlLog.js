(function(vc) {

    vc.extends({
        data: {
            showAccessControlLogInfo: {
                logId: '',
                reqParam: '',
                resParam: '',

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('showAccessControlLog', 'openShowAccessControlLogModal', function(_param) {
                vc.copyObject(_param, $that.showAccessControlLogInfo);
                $that.loadShowAccessControlLogInfo();
                $('#showAccessControlLogModel').modal('show');
            });
        },
        methods: {

            loadShowAccessControlLogInfo: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        logId: $that.showAccessControlLogInfo.logId,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControlLog.listAccessControlLogParam',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.showAccessControlLogInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            clearShowAccessControlLogInfo: function() {
                vc.component.showAccessControlLogInfo = {
                    logId: '',
                    machineName: '',
                    machineCode: '',
                    logAction: '',
                    userId: '',
                    userName: '',
                    state: '',

                };
            }
        }
    });

})(window.vc);