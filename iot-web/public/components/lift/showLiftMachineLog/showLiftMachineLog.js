(function(vc) {

    vc.extends({
        data: {
            showLiftMachineLogInfo: {
                logId: '',
                reqParam: '',
                resParam: '',
            }
        },
        _initMethod: function() {
        },
        _initEvent: function() {
            vc.on('showLiftMachineLog', 'openShowLiftMachineLogModal', function(_param) {
                vc.copyObject(_param, $that.showLiftMachineLogInfo);
                $('#showLiftMachineLogModel').modal('show');
            });
        },
        methods: {
        }
    });
})(window.vc);