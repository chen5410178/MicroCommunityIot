(function(vc, vm) {

    vc.extends({
        data: {
            deleteCityAreaInfo: {

            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('deleteCityArea', 'openDeleteCityAreaModal', function(_params) {
                $that.deleteCityAreaInfo = _params;
                $('#deleteCityAreaModel').modal('show');
            });
        },
        methods: {
            deleteCityArea: function() {
                vc.http.apiPost(
                    '/area.deleteCityArea',
                    JSON.stringify($that.deleteCityAreaInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteCityAreaModel').modal('hide');
                            vc.emit('cityAreaTree', 'refreshTree', {});
                            return;
                        }
                        vc.toast(_json.msg);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);

                    });
            },
            closeDeleteCityAreaModel: function() {
                $('#deleteCityAreaModel').modal('hide');
            }
        }
    });

})(window.vc, window.$that);