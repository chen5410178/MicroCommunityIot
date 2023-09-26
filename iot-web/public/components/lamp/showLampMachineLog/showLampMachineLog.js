(function(vc) {

    vc.extends({
        data: {
            showLampMachineLogInfo: {
                logId: '',
                reqParam: '',
                resParam: '',

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('showLampMachineLog', 'openShowLampMachineLogModal', function(_param) {
                vc.copyObject(_param, $that.showLampMachineLogInfo);
                $('#showLampMachineLogModel').modal('show');
            });
        },
        methods: {

        }
    });

})(window.vc);