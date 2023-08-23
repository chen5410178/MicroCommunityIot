(function(vc, vm) {

    vc.extends({
        data: {
            deleteParkingSpaceInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteParkingSpace', 'openDeleteParkingSpaceModal', function(_params) {

                $that.deleteParkingSpaceInfo = _params;
                $('#deleteParkingSpaceModel').modal('show');

            });
        },
        methods: {
            deleteParkingSpace: function() {
                $that.deleteParkingSpaceInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    'parkingSpace.deleteParkingSpace',
                    JSON.stringify($that.deleteParkingSpaceInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteParkingSpaceModel').modal('hide');
                            vc.emit('parkingSpace', 'listParkingSpace', {});
                            return;
                        }
                        vc.message(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(json);

                    });
            },
            closeDeleteParkingSpaceModel: function() {
                $('#deleteParkingSpaceModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);