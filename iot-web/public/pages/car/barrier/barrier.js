/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            barrierInfo: {
                barriers: [],
                parkingBoxs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                machineId: '',
                conditions: {
                    machineCode: '',
                    machineName: '',
                    boxId: '',

                }
            }
        },
        _initMethod: function () {
            $that._listParkingBoxs();
            $that._listBarriers(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('barrier', 'listBarrier', function (_param) {
                $that._listBarriers(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listBarriers(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listBarriers: function (_page, _rows) {

                $that.barrierInfo.conditions.page = _page;
                $that.barrierInfo.conditions.row = _rows;
                $that.barrierInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.barrierInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/barrier.listBarrier',
                    param,
                    function (json, res) {
                        let _barrierInfo = JSON.parse(json);
                        $that.barrierInfo.total = _barrierInfo.total;
                        $that.barrierInfo.records = _barrierInfo.records;
                        $that.barrierInfo.barriers = _barrierInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.barrierInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddBarrierModal: function () {
                vc.emit('addBarrier', 'openAddBarrierModal', {});
            },
            _openEditBarrierModel: function (_barrier) {
                vc.emit('editBarrier', 'openEditBarrierModal', _barrier);
            },
            _openDeleteBarrierModel: function (_barrier) {
                vc.emit('deleteBarrier', 'openDeleteBarrierModal', _barrier);
            },
            _queryBarrierMethod: function () {
                $that._listBarriers(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.barrierInfo.moreCondition) {
                    $that.barrierInfo.moreCondition = false;
                } else {
                    $that.barrierInfo.moreCondition = true;
                }
            },
            _listParkingBoxs: function (_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                $that.barrierInfo.parkingBoxs = [{
                    boxId: '',
                    boxName: '全部'
                }]
                //发送get请求
                vc.http.apiGet('/parkingBox.listParkingBox',
                    param,
                    function (json, res) {
                        var _parkingBoxInfo = JSON.parse(json);
                        _parkingBoxInfo.data.forEach(item => {
                            $that.barrierInfo.parkingBoxs.push(item);
                        });

                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchParkingBox: function (_box) {
                $that.barrierInfo.conditions.boxId = _box.boxId;
                $that._listBarriers(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _openVisitQrCode: function (_barrier) {
                var param = {
                    params: {
                        communityId: vc.getCurrentCommunity().communityId,
                    }
                };
                //发送get请求
                vc.http.apiGet('/visit.getVisitQrCodeUrl',
                    param,
                    function (json, res) {
                        let _info = JSON.parse(json);
                        vc.emit('viewQrCode', 'openQrCodeModal', {
                            title: _barrier.machineName+"访客码",
                            url: _info.data.url+'&machineId='+_barrier.machineId,
                            remark: '道闸口访客码，扫码后自动关联待进场车辆信息',
                        })
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            }

        }
    });
})(window.vc);
