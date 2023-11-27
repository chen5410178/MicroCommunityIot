(function(vc) {

    vc.extends({
        data: {
            showInstrumentLogSensorInfo: {
                sensorValue: '',
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('showInstrumentLogSensor', 'openViewInstrumentLogSensorModal', function(_param) {
                vc.copyObject(_param, $that.showInstrumentLogSensorInfo);
                $('#showInstrumentLogSensorModel').modal('show');
            });
        },
        methods: {

        }
    });
})(window.vc);