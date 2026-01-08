(function(vc, vm) {
    vc.extends({
        data: {
            deleteTempInnerCarInfo: {}
        },
        _initMethod: function() {},
        _initEvent: function() {
            vc.on('deleteTempInnerCar', 'openDeleteTempInnerCarModal', function(_params) {
                $that.deleteTempInnerCarInfo = _params;
                $('#deleteTempInnerCarModel').modal('show');
            });
        },
        methods: {
            deleteTempInnerCar: function() {
                $that.deleteTempInnerCarInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/car.deleteTempInnerCar',
                    JSON.stringify($that.deleteTempInnerCarInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteTempInnerCarModel').modal('hide');
                            vc.emit('tempInnerCar', 'listTempInnerCar', {});
                            vc.toast("删除成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteTempInnerCarModel: function() {
                $('#deleteTempInnerCarModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);

