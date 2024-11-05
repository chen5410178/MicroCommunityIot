(function (vc) {
    vc.extends({
        data: {
            cameraAlterInfo: {
                videoAlters: [],
                refresh: false
            },

        },
        _initMethod: function () {
            if ($that.cameraAlterInfo.refresh) {
                return;
            }
            $that._reloadCameraAlter();
            $that.cameraAlterInfo.refresh = true;
        },
        _initEvent: function () {

        },
        methods: {
            _reloadCameraAlter: function () {

                $that._listVideoAlters();
                setTimeout(function () {
                    $that._reloadCameraAlter();
                }, 30000);

            },
            _listVideoAlters: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 10,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/ai.getAiAlter',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.cameraAlterInfo.videoAlters = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);