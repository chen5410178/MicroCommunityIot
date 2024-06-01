(function(vc) {

    vc.extends({
        data: {
            showMeterMachineLogInfo: {
                logId: '',
                reqParam: '',
                resParam: '',

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('showMeterMachineLog', 'openShowMeterMachineLogModal', function(_param) {
                vc.copyObject(_param, $that.showMeterMachineLogInfo);
              //  $that.loadShowLogInfo();
                $('#showMeterMachineLogModel').modal('show');
            });
        },
        methods: {

            loadShowLogInfo: function() {
                let param = {
                    params: {
                        page: 1,
                        row: 1,
                        logId: $that.showMeterMachineLogInfo.logId,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/workLicense.listMeterMachineLog',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.showMeterMachineLogInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            clearShowLogInfo: function() {
                $that.showMeterMachineLogInfo = {
                    logId: '',
                    reqParam: '',
                    resParam: '',

                };
            }
        }
    });

})(window.vc);