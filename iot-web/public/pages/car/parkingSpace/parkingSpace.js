/**
    入驻小区
**/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            parkingSpaceInfo: {
                parkingSpaces: [],
                parkingAreas: [],
                total: 0,
                records: 1,
                moreCondition: false,
                psId: '',
                conditions: {
                    num: '',
                    paId: '',
                    state: '',
                    parkingType: '',

                }
            }
        },
        _initMethod: function() {
            $that._loadListParkingAreas();
            $that._listParkingSpaces(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function() {

            vc.on('parkingSpace', 'listParkingSpace', function(_param) {
                $that._listParkingSpaces(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that._listParkingSpaces(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listParkingSpaces: function(_page, _rows) {
                $that.parkingSpaceInfo.conditions.page = _page;
                $that.parkingSpaceInfo.conditions.row = _rows;
                $that.parkingSpaceInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: $that.parkingSpaceInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/parkingSpace.listParkingSpace',
                    param,
                    function(json, res) {
                        let _parkingSpaceInfo = JSON.parse(json);
                        $that.parkingSpaceInfo.total = _parkingSpaceInfo.total;
                        $that.parkingSpaceInfo.records = _parkingSpaceInfo.records;
                        $that.parkingSpaceInfo.parkingSpaces = _parkingSpaceInfo.data;
                        vc.emit('pagination', 'init', {
                            total: $that.parkingSpaceInfo.records,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddParkingSpaceModal: function() {
                vc.emit('addParkingSpace', 'openAddParkingSpaceModal', {});
            },
            _openEditParkingSpaceModel: function(_parkingSpace) {
                vc.emit('editParkingSpace', 'openEditParkingSpaceModal', _parkingSpace);
            },
            _openDeleteParkingSpaceModel: function(_parkingSpace) {
                vc.emit('deleteParkingSpace', 'openDeleteParkingSpaceModal', _parkingSpace);
            },
            _queryParkingSpaceMethod: function() {
                $that._listParkingSpaces(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function() {
                if ($that.parkingSpaceInfo.moreCondition) {
                    $that.parkingSpaceInfo.moreCondition = false;
                } else {
                    $that.parkingSpaceInfo.moreCondition = true;
                }
            },
            _loadListParkingAreas: function(_page, _rows) {

                let param = {
                    params: {
                        page: 1,
                        row: 100,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/parkingArea.listParkingArea',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.parkingSpaceInfo.parkingAreas = _json.data;

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _viewOwnerFace: function(_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
            _viewParkingSpaceVideo:function(_parkingSpace){
                vc.emit('playMonitorVideo', 'openPlayMonitorVideoModal', {
                    machineId:_parkingSpace.monitorId,
                    machineName:_parkingSpace.monitorName,
                    communityId:_parkingSpace.communityId
                });
            }


        }
    });
})(window.vc);