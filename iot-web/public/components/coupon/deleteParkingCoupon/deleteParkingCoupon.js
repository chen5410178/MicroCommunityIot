(function(vc, vm) {

    vc.extends({
        data: {
            deleteParkingCouponInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteParkingCoupon', 'openDeleteParkingCouponModal', function(_params) {

                $that.deleteParkingCouponInfo = _params;
                $('#deleteParkingCouponModel').modal('show');

            });
        },
        methods: {
            deleteParkingCoupon: function() {
                $that.deleteParkingCouponInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/parkingCoupon.deleteParkingCoupon',
                    JSON.stringify($that.deleteParkingCouponInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteParkingCouponModel').modal('hide');
                            vc.emit('parkingCoupon', 'listParkingCoupon', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteParkingCouponModel: function() {
                $('#deleteParkingCouponModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);