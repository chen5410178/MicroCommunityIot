/**
 入驻小区
 **/
(function(vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROW = 10;
    vc.extends({
        data: {
            accessControlFloorInfo: {
                accessControls: [],
                total: 0,
                records: 0,
                moreCondition: false,
                conditions: {
                    floorId: '',
                    unitId: '',
                    machineName: '',
                },
                currentPage: DEFAULT_PAGE,

            }
        },
        _initMethod: function() {


        },
        _initEvent: function() {
            vc.on('accessControlFloor', 'switchFloor', function(_param) {
                $that.accessControlFloorInfo.conditions.floorId = _param.floorId;
                $that.accessControlFloorInfo.conditions.unitId = '';
                $that.listAccessControlFloor(DEFAULT_PAGE, DEFAULT_ROW);
            });
            vc.on('accessControlFloor', 'switchUnit', function(_param) {
                $that.accessControlFloorInfo.conditions.floorId = '';
                $that.accessControlFloorInfo.conditions.unitId = _param.unitId;
                $that.listAccessControlFloor(DEFAULT_PAGE, DEFAULT_ROW);
            });
            vc.on('accessControlFloor', 'listAccessControlFloor', function(_param) {
                $that.listAccessControlFloor($that.accessControlFloorInfo.currentPage, DEFAULT_ROW);
            });
            vc.on('accessControlFloor', 'loadData', function(_param) {
                $that.listAccessControlFloor($that.accessControlFloorInfo.currentPage, DEFAULT_ROW);
            });
            vc.on('pagination', 'page_event', function(_currentPage) {
                $that.accessControlFloorInfo.currentPage = _currentPage;
                $that.listAccessControlFloor(_currentPage, DEFAULT_ROW);
            });
        },
        methods: {
            listAccessControlFloor: function(_page, _row) {
                $that.accessControlFloorInfo.conditions.page = _page;
                $that.accessControlFloorInfo.conditions.row = _row;
                $that.accessControlFloorInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                let param = {
                    params: JSON.parse(JSON.stringify($that.accessControlFloorInfo.conditions))
                };

                //发送get请求
                vc.http.apiGet('/accessControlFloor.listAccessControlFloor',
                    param,
                    function(json, res) {
                        let listAccessControlFloorData = JSON.parse(json);
                        $that.accessControlFloorInfo.total = listAccessControlFloorData.total;
                        $that.accessControlFloorInfo.records = listAccessControlFloorData.records;
                        $that.accessControlFloorInfo.accessControls = listAccessControlFloorData.data;
                        vc.emit('pagination', 'init', {
                            total: $that.accessControlFloorInfo.records,
                            dataCount: $that.accessControlFloorInfo.total,
                            currentPage: _page
                        });
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },

            _openAuthorizeAccessControlModel: function(_accessControlFloor) {
                let _floorId = $that.accessControlFloorInfo.conditions.floorId;
                let _unitId = $that.accessControlFloorInfo.conditions.unitId;

                if (!_floorId && !_unitId) {
                    vc.toast('请选择左边需要授权的楼栋或者单元');
                    return;
                }

                vc.emit('authorizeAccessControl', 'openAddAccessControlModal', {
                    floorId:_floorId,
                    unitId:_unitId,
                });
            },
            _openDelAccessControlFloorModel: function(_accessControlFloor) {
                vc.emit('deleteAccessControlFloor', 'openAccessControlFloorModel', _accessControlFloor);
            },

            _queryAccessControlFloorMethod: function() {
                $that.listAccessControlFloor(DEFAULT_PAGE, DEFAULT_ROW);
            },


            _moreCondition: function() {
                if ($that.accessControlFloorInfo.moreCondition) {
                    $that.accessControlFloorInfo.moreCondition = false;
                } else {
                    $that.accessControlFloorInfo.moreCondition = true;
                }
            },

        }
    });
})(window.vc);