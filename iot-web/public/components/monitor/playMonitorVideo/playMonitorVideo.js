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

            _playWsVideo: function () {
                let _protocol = window.location.protocol;
                let url = window.location.host  + "/ws/" + this.clientId;
                if (_protocol.startsWith('https')) {
                    url =
                        "wss://" + url;
                } else {
                    url =
                        "ws://" + url;
                }
                let image = document.getElementById("receiver1Div");
                let jessibuca = new Jessibuca({
                    container: image,
                    videoBuffer: 0.2,
                    isResize: false,
                });
                jessibuca.onLoad = function () {
                    this.play(wsUrl);
                };
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
