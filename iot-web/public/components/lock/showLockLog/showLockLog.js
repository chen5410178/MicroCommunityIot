(function(vc) {

    vc.extends({
        data: {
            showLockLogInfo: {
                logId: '',
                reqParam: '',
                resParam: '',
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('showLockLog', 'openShowLockLogModal', function(_param) {
                $that._cleanShowLockLogInfo();
                vc.copyObject(_param, $that.showLockLogInfo);
                $('#showLockLogModel').modal('show');
            });
        },
        methods: {
            _cleanShowLockLogInfo: function () {
                $that.showLockLogInfo = {
                    logId: '',
                    reqParam: '',
                    resParam: '',
                }
            }
        }
    });

})(window.vc);