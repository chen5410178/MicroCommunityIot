(function (vc) {

    vc.extends({
        data: {
            playMonitorVideoInfo: {
                machineName: '',
                machineId: '',
                communityId: '',
                remark: '',
            }
        },
        _initMethod: function () {

        },
        _initEvent: function () {
            vc.on('playMonitorVideo', 'openPlayMonitorVideoModal', function () {
                $('#playMonitorVideoModel').modal('show');
            });
        },
        methods: {
            
            _playWsVideo:function(){

            },
            
            clearPlayMonitorVideoInfo: function () {
                $that.playMonitorVideoInfo = {
                    machineName: '',
                    machineId: '',
                    communityId: '',
                    remark: '',
                };
            }
        }
    });
})(window.vc);
