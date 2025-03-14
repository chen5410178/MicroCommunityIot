/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            adminCarInoutInfo: {
                carIns: [],
                boxId: '',
                paId: '',
                state: '',
                carNum: '',
                startTime: '',
                endTime: '',
                carType: '',
                machineId: '',
                conditions: {
                    machineId: '',
                    name: '',
                    openTypeCd: '',
                    tel: '',
                    state: '',
                    queryStartTime: '',
                    queryEndTime: '',
                    communityId: '',
                }
            }
        },
        _initMethod: function () {
            vc.on('selectAdminCommunity', 'changeCommunity', function (_community) {
                $that.adminCarInoutInfo.conditions.communityId = _community.communityId;
                $that._listAdminCarInouts();
                $that._listAdminCarInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            })
            $that._listAdminCarInouts();
            $that._listAdminCarInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.initDateTime('carInoutsStartTime', function (_value) {
                $that.adminCarInoutInfo.conditions.queryStartTime = _value;
            });
            vc.initDateTime('carInoutsEndTime', function (_value) {
                $that.adminCarInoutInfo.conditions.queryEndTime = _value;
            });
        },
        _initEvent: function () {

            vc.on('adminCarInout', 'listAdminCarInout', function (_param) {
                $that._listAdminCarInouts(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('adminCarInout', 'paginationPlus', 'page_event',
                function (_currentPage) {
                    $that._listAdminCarInouts(_currentPage, DEFAULT_ROWS);
                });
        },
        methods: {
            _listAdminCarInouts: function (_page, _rows) {

                let param = {
                    params: {
                        page: _page,
                        row: _rows,
                        boxId: $that.adminCarInoutInfo.boxId,
                        state: $that.adminCarInoutInfo.state,
                        carNumLike: $that.adminCarInoutInfo.carNum,
                        startTime: $that.adminCarInoutInfo.startTime,
                        endTime: $that.adminCarInoutInfo.endTime,
                        carType: $that.adminCarInoutInfo.carType,
                        paId: $that.adminCarInoutInfo.paId
                    }
                };
                //发送get请求
                vc.http.apiGet('/carInoutDetail.listAdminCarInoutDetail',
                    param,
                    function (json) {
                        let _json = JSON.parse(json);
                        $that.adminCarInoutInfo.total = _json.total;
                        $that.adminCarInoutInfo.records = _json.records;
                        $that.adminCarInoutInfo.carIns = _json.data;
                        vc.emit('adminCarInout', 'paginationPlus', 'init', {
                            total: _json.records,
                            currentPage: _page
                        });
                    },
                    function () {
                        console.log('请求失败处理');
                    }
                );
            },

            _queryAdminCarInoutMethod: function () {
                $that._listAdminCarInouts(DEFAULT_PAGE, DEFAULT_ROWS);

            },

            _viewOwnerFace: function (_url) {
                vc.emit('viewImage', 'showImage', {
                    url: _url
                });
            },
        }
    });
})(window.vc);