(function (vc, vm) {
    vc.extends({
        data: {
            deleteUnitInfo: {
                _currentFloorId: '',
                _currentUnitId: ''
            }
        },
        _initMethod: function () {
        },
        _initEvent: function () {
            vc.on('deleteUnit', 'openUnitModel', function (_params) {
                $that.deleteUnitInfo._currentFloorId = _params.floorId;
                $that.deleteUnitInfo._currentUnitId = _params.unitId;
                $('#deleteUnitModel').modal('show');
            });
        },
        methods: {
            deleteUnit: function () {
                var param = {
                    floorId: $that.deleteUnitInfo._currentFloorId,
                    unitId: $that.deleteUnitInfo._currentUnitId,
                    communityId: vc.getCurrentCommunity().communityId
                }
                vc.http.apiPost(
                    '/unit.deleteUnit',
                    JSON.stringify(param),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#deleteUnitModel').modal('hide');
                            vc.emit('unit', 'loadUnit', {
                                floorId: $that.deleteUnitInfo._currentFloorId
                            });
                            vc.emit('floorUnitTree', 'refreshTree', {
                                floorId: $that.deleteUnitInfo._currentFloorId
                            })
                            vc.toast("删除成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(json);
                    });
            },
            closeDeleteUnitModel: function () {
                $('#deleteUnitModel').modal('hide');
            }
        }
    });
})(window.vc, window.$that);