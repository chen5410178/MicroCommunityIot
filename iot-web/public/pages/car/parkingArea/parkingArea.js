/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingAreaInfo: {
                parkingAreas: [],
                total: 0,
                records: 1,
                moreCondition: false,
                paId: '',
                conditions: {
                    paId: '',
                    num: '',
                    typeCd: '',
                }
            }
        },
        _initMethod: function() {
            $that._listParkingAreas(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('parkingArea', 'listParkingArea', function(_param) {
                $that._listParkingAreas(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listParkingAreas(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingAreas: function(_page, _rows) {

                $that.parkingAreaInfo.conditions.page = _page;
                $that.parkingAreaInfo.conditions.row = _rows;
                $that.parkingAreaInfo.conditions.communityId = vc.getCurrentCommunity().communityId;

                let param = {
                    params: $that.parkingAreaInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/parkingArea.listParkingArea',
                    param,
                    function(json, res) {
                        var _parkingAreaInfo = JSON.parse(json);
                        $that.parkingAreaInfo.total = _parkingAreaInfo.total;
                        $that.parkingAreaInfo.records = _parkingAreaInfo.records;
                        $that.parkingAreaInfo.parkingAreas = _parkingAreaInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingAreaInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddParkingAreaModal: function() {
                vc.emit('addParkingArea', 'openAddParkingAreaModal', {});
            },
            _openEditParkingAreaModel: function(_parkingArea) {
                vc.emit('editParkingArea', 'openEditParkingAreaModal', _parkingArea);
            },
            _openDeleteParkingAreaModel: function(_parkingArea) {
                vc.emit('deleteParkingArea', 'openDeleteParkingAreaModal', _parkingArea);
            },
            _queryParkingAreaMethod: function() {
                $that._listParkingAreas(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.parkingAreaInfo.moreCondition) {
                    $that.parkingAreaInfo.moreCondition = false;
                } else {
                    $that.parkingAreaInfo.moreCondition = true;
                }
            },
            _openParkingAreaTotalControl: function (_parkingArea) {
                vc.jumpToPage('/#/pages/car/parkingAreaTotalControl?paId=' + _parkingArea.paId);
            }

        }
    });
})(window.vc);