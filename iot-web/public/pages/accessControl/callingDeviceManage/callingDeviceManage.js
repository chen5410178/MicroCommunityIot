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
            vc.component._listCallingDevices(DEFAULT_PAGE, DEFAULT_ROWS);
            vc.component._listCallingPlatforms();
        },
        _initEvent: function () {

            vc.on('callingDeviceManage', 'listCallingDevice', function (_param) {
                vc.component._listCallingDevices(DEFAULT_PAGE, DEFAULT_ROWS);
            });
            vc.on('pagination', 'page_event', function (_currentPage) {
                vc.component._listCallingDevices(_currentPage, DEFAULT_ROWS);
            });
        },
        methods: {
            _listCallingDevices: function (_page, _rows) {
                vc.component.callingDeviceManageInfo.conditions.page = _page;
                vc.component.callingDeviceManageInfo.conditions.row = _rows;
                vc.component.callingDeviceManageInfo.conditions.communityId = vc.getCurrentCommunity().communityId;
                var param = {
                    params: vc.component.callingDeviceManageInfo.conditions
                };

                //发送get请求
                vc.http.apiGet('callingDevice.listCallingDevice',
                    param,
                    function (json, res) {
                        var _callingDeviceManageInfo = JSON.parse(json);
                        vc.component.callingDeviceManageInfo.total = _callingDeviceManageInfo.total;
                        vc.component.callingDeviceManageInfo.records = _callingDeviceManageInfo.records;
                        vc.component.callingDeviceManageInfo.callingDevices = _callingDeviceManageInfo.data;
                        vc.emit('pagination', 'init', {
                            total: vc.component.callingDeviceManageInfo.records,
                            currentPage: _page
                        });
                    }, function (errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            _listCallingPlatforms: function () {
                var param = {
                    params: {page:1,row:50,communityId:vc.getCurrentCommunity().communityId}
                };
                //发送get请求
                vc.http.apiGet('callingPlatform.listCallingPlatform',
                    param,
                    function (json, res) {
                        var _callingPlatformManageInfo = JSON.parse(json);
                        vc.component.callingDeviceManageInfo.callingPlatforms = _callingPlatformManageInfo.data;
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
                vc.component._listCallingDevices(DEFAULT_PAGE, DEFAULT_ROWS);

            },
            _moreCondition: function () {
                if (vc.component.callingDeviceManageInfo.moreCondition) {
                    vc.component.callingDeviceManageInfo.moreCondition = false;
                } else {
                    vc.component.callingDeviceManageInfo.moreCondition = true;
                }
            }


        }
    });
})(window.vc);
