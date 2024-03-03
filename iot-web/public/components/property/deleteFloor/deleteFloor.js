(function (vc) {
    vc.extends({
        data: {
            deleteFloorInfo: {}
        },
        _initEvent: function () {
            vc.on('deleteFloor', 'openFloorModel', function (_floorInfo) {
                $that.deleteFloorInfo = _floorInfo;
                $('#deleteFloorModel').modal('show');
            });
        },
        methods: {
            closeDeleteFloorModel: function () {
                $('#deleteFloorModel').modal('hide');
            },
            deleteFloor: function () {
                $that.deleteFloorInfo.communityId = vc.getCurrentCommunity().communityId;
                vc.http.apiPost(
                    '/floor.deleteFloor',
                    JSON.stringify($that.deleteFloorInfo), {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteFloorModel').modal('hide');
                            vc.emit('listFloor', 'listFloorData', {});
                            vc.emit('floorUnitTree', 'refreshTree', {});
                            vc.toast("删除成功")
                            return;
                        } else {
                            vc.toast(_json.msg)
                        }
                        $that.deleteFloornfo.errorInfo = json;
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        $that.deleteFloornfo.errorInfo = errInfo;
                    });
            }
        }
    });
})(window.vc);