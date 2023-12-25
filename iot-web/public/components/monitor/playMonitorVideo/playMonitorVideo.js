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
            vc.on('playMonitorVideo', 'openPlayMonitorVideoModal', function (_machine) {
                vc.copyObject(_machine, $that.playMonitorVideoInfo);
                $('#playMonitorVideoModel').modal('show');
                $that._playWsVideo();
            });
        },
        methods: {

            _playWsVideo: function () {
                let _protocol = window.location.protocol;
                let url = window.location.host + "/ws/videoStream/" + $that.playMonitorVideoInfo.machineId + "/" + vc.uuid();
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
                    isFlv:true,
                    useMSE:true,
                    debug:true
                });
                jessibuca.onLoad = function () {
                    this.play(url);
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
