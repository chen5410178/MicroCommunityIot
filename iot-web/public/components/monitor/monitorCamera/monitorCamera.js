(function (vc) {
    vc.extends({
        data: {
            monitorCameraInfo: {
                collects: [],
            },

        },
        _initMethod: function () {

            vc.emit('videoTree', 'initVideoTree', {
                callName: 'monitorCamera'
            });

            $that._listVideoCollects();
           
        },
        _initEvent: function () {
            vc.on('monitorCamera', 'selectVideo', function (_param) {
                vc.emit('monitorVideo', 'selectVideo',_param);
            });
        },
        methods: {
            _listVideoCollects: function (_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:1000,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/monitor.listMonitorCollect',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.monitorCameraInfo.collects = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _selectCollectVideo:function(_video){
                vc.emit('monitorVideo', 'selectVideo',_video);
            }
        }
    });

})(window.vc);