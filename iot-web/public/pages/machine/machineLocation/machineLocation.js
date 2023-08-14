/**
    入驻小区
**/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            machineLocationInfo: {
                machineLocations: [],
                total: 0,
                records: 1,
                moreCondition: false,
                locationId: '',
                conditions: {
                    locationName: '',
                    locationType: '',
                    communityId:vc.getCurrentCommunity().communityId
                }
            }
        },
        _initMethod: function () {
            $that._listMachineLocations(DEFAULT_PAGE, DEFAULT_ROWS);
        },
        _initEvent: function () {

            vc.on('machineLocation', 'listMachineLocation', function (_param) {
                $that._listMachineLocations(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listMachineLocations(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listMachineLocations: function (_page, _rows) {

                $that.machineLocationInfo.conditions.page = _page;
                $that.machineLocationInfo.conditions.row = _rows;
                let param = {
                    params: $that.machineLocationInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('machineLocation.listMachineLocations',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.machineLocationInfo.total = _json.total;
                        $that.machineLocationInfo.records = _json.records;
                        $that.machineLocationInfo.machineLocations = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.machineLocationInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddMachineLocationModal: function () {
                vc.emit('addMachineLocation', 'openAddMachineLocationModal', {});
            },
            _openEditMachineLocationModel: function (_machineLocation) {
                vc.emit('editMachineLocation', 'openEditMachineLocationModal', _machineLocation);
            },
            _openDeleteMachineLocationModel: function (_machineLocation) {
                vc.emit('deleteMachineLocation', 'openDeleteMachineLocationModal', _machineLocation);
            },
            _queryMachineLocationMethod: function () {
                $that._listMachineLocations(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.machineLocationInfo.moreCondition) {
                    $that.machineLocationInfo.moreCondition = false;
                } else {
                    $that.machineLocationInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
