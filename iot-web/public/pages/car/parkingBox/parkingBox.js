/**
 入驻小区
 **/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingBoxInfo: {
                parkingBoxs: [],
                total: 0,
                records: 1,
                moreCondition: false,
                boxId: '',
                conditions: {
                    boxId: '',
                    boxName: '',
                    tempCarIn: '',
                    communityId: vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function() {
            $that._listParkingBoxs(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {
            vc.on('parkingBox', 'listParkingBox', function(_param) {
                $that._listParkingBoxs(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listParkingBoxs(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingBoxs: function(_page, _rows) {
                $that.parkingBoxInfo.conditions.page = _page;
                $that.parkingBoxInfo.conditions.row = _rows;
                var param = {
                    params: $that.parkingBoxInfo.conditions
                };
                param.params.boxId = param.params.boxId.trim();
                param.params.boxName = param.params.boxName.trim();
                //发送get请求
                vc.http.apiGet('/parkingBox.listParkingBox',
                    param,
                    function(json, res) {
                        var _parkingBoxInfo = JSON.parse(json);
                        $that.parkingBoxInfo.total = _parkingBoxInfo.total;
                        $that.parkingBoxInfo.records = _parkingBoxInfo.records;
                        $that.parkingBoxInfo.parkingBoxs = _parkingBoxInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingBoxInfo.records,
                            dataCount: $that.parkingBoxInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddParkingBoxModal: function() {
                vc.emit('addParkingBox', 'openAddParkingBoxModal', {});
            },
            _openEditParkingBoxModel: function(_parkingBox) {
                vc.emit('editParkingBox', 'openEditParkingBoxModal', _parkingBox);
            },
            _openDeleteParkingBoxModel: function(_parkingBox) {
                vc.emit('deleteParkingBox', 'openDeleteParkingBoxModal', _parkingBox);
            },
            //查询
            _queryParkingBoxMethod: function() {
                $that._listParkingBoxs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            //重置
            _resetParkingBoxMethod: function() {
                $that.parkingBoxInfo.conditions.boxId = "";
                $that.parkingBoxInfo.conditions.boxName = "";
                $that.parkingBoxInfo.conditions.tempCarIn = "";
                $that._listParkingBoxs(DEFAULT_PAGE, DEFAULT_ROWS);
            },
            _moreCondition: function() {
                if ($that.parkingBoxInfo.moreCondition) {
                    $that.parkingBoxInfo.moreCondition = false;
                } else {
                    $that.parkingBoxInfo.moreCondition = true;
                }
            },
            _parkingBoxArea: function(_parkingBox) {
                vc.jumpToPage('/#/pages/car/parkingBoxArea?boxId=' + _parkingBox.boxId + "&boxName=" + _parkingBox.boxName);
            },
            _openParkingAreaControl: function(_parkingBox) {
                vc.jumpToPage('/#/pages/car/parkingAreaControl?boxId=' + _parkingBox.boxId + "&paId=" + _parkingBox.paId);
            }
        }
    });
})(window.vc);