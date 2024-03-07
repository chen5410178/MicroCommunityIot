/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingRegionInfo: {
                parkingRegions: [],
                total: 0,
                records: 1,
                moreCondition: false,
                prId: '',
                parkingAreas:[],
                conditions: {
                    prId: '',
                    regionCode: '',
                    paId: '',

                }
            }
        },
        _initMethod: function () {
            $that._listParkingAreas(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('parkingRegion', 'listParkingRegion', function (_param) {
                $that._listParkingRegions(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listParkingRegions(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingRegions: function (_page, _rows) {

                $that.parkingRegionInfo.conditions.page = _page;
                $that.parkingRegionInfo.conditions.row = _rows;
                $that.parkingRegionInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.parkingRegionInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/parkingRegion.listParkingRegion',
                    param,
                    function (json, res) {
                        var _parkingRegionInfo = JSON.parse(json);
                        $that.parkingRegionInfo.total = _parkingRegionInfo.total;
                        $that.parkingRegionInfo.records = _parkingRegionInfo.records;
                        $that.parkingRegionInfo.parkingRegions = _parkingRegionInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingRegionInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddParkingRegionModal: function () {
                vc.emit('addParkingRegion', 'openAddParkingRegionModal', {
                    paId:$that.parkingRegionInfo.conditions.paId
                });
            },
            _openEditParkingRegionModel: function (_parkingRegion) {
                vc.emit('editParkingRegion', 'openEditParkingRegionModal', _parkingRegion);
            },
            _openDeleteParkingRegionModel: function (_parkingRegion) {
                vc.emit('deleteParkingRegion', 'openDeleteParkingRegionModal', _parkingRegion);
            },
            _queryParkingRegionMethod: function () {
                $that._listParkingRegions(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.parkingRegionInfo.moreCondition) {
                    $that.parkingRegionInfo.moreCondition = false;
                } else {
                    $that.parkingRegionInfo.moreCondition = true;
                }
            },
            _listParkingAreas: function(_page, _rows) {
                let param = {
                    params: {
                        page:1,
                        row:30,
                        communityId:vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/parkingArea.listParkingArea',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingRegionInfo.parkingAreas = _json.data;
                        if(_json.data && _json.data.length>0){
                            $that.swatchParkingArea(_json.data[0]);
                        }
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            swatchParkingArea:function(_area){
                $that.parkingRegionInfo.conditions.paId = _area.paId;
                $that._listParkingRegions(DEFAULT_PAGE, DEFAULT_ROWS);
            }
        }
    });
})(window.vc);
