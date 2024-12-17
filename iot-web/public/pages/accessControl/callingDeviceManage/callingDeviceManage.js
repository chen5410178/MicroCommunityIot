/**
 入驻小区
 **/
(function (vc) {
    var DEFAULT_PAGE = 1;
    var DEFAULT_ROWS = 10;
    vc.extends({
        data: {
            callingDeviceManageInfo: {
                callingDevices: [],
                callingPlatforms: [],
                total: 0,
                records: 1,
                moreCondition: false,
                deviceId: '',
                conditions: {
                    deviceName: '',
                    callingNum: '',
                }
            }
        },
        _initMethod: function () {
            $that._listCallingDevices(DEFAULT_PAGE, DEFAULT_ROWS);
            $that._listCallingPlatforms();
        },
        _initEvent: function () {

            vc.on('callingDeviceManage', 'listCallingDevice', function (_param) {
                $that._listCallingDevices(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                $that._listCallingDevices(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCallingDevices: function (_page, _rows) {
                $that.callingDeviceManageInfo.conditions.page = _page;
                $that.callingDeviceManageInfo.conditions.row = _rows;
                $that.callingDeviceManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: $that.callingDeviceManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('/callingDevice.listCallingDevice',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.callingDeviceManageInfo.total = _json.total;
                        $that.callingDeviceManageInfo.records = _json.records;
                        $that.callingDeviceManageInfo.callingDevices = _json.data;
                        vc.emit('pagination', 'init', {
                            total: $that.callingDeviceManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listCallingPlatforms: function () {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };
                //发送get请求
                vc.http.apiGet('/callingPlatform.listCallingPlatform',
                    param,
                    function (json, res) {
                        let _json = JSON.parse(json);
                        $that.callingDeviceManageInfo.callingPlatforms = _json.data;
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _openAddCallingDeviceModal: function () {
                vc.emit('addCallingDevice', 'openAddCallingDeviceModal', {});
            },
            _openEditCallingDeviceModel: function (_callingDevice) {
                vc.emit('editCallingDevice', 'openEditCallingDeviceModal', _callingDevice);
            },
            _openDeleteCallingDeviceModel: function (_callingDevice) {
                vc.emit('deleteCallingDevice', 'openDeleteCallingDeviceModal', _callingDevice);
            },
            _queryCallingDeviceMethod: function () {
                $that._listCallingDevices(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if ($that.callingDeviceManageInfo.moreCondition) {
                    $that.callingDeviceManageInfo.moreCondition = false;
                } else {
                    $that.callingDeviceManageInfo.moreCondition = true;
                }
            }
        }
    });
})(window.vc);
