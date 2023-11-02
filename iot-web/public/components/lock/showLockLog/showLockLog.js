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
                vc.copyObject(_param, $that.showLockLogInfo);
                $('#showLockLogModel').modal('show');
            });
        },
        methods: {

        }
    });

})(window.vc);