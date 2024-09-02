(function(vc) {
    vc.extends({
        propTypes: {
            emitChooseCallingDevice: vc.propTypes.string,
            emitLoadData: vc.propTypes.string
        },
        data: {
            chooseCallingDeviceInfo: {
                devices: [],
                _currentDeviceName: '',
            }
        },
        _initMethod: function() {},
        _initEvent: function() {
            vc.on('chooseCallingDevice', 'openChooseCallingDeviceModel', function(_param) {
                $('#chooseCallingDeviceModel').modal('show');
                vc.component._refreshChooseCallingDeviceInfo();
                vc.component._loadAllDeviceInfo(1, 10, '');
            });
            vc.on('chooseCallingDevice', 'paginationPlus', 'page_event', function(_currentPage) {
                vc.component._loadAllDeviceInfo(_currentPage, 10);
            });
        },
        methods: {
            _loadAllDeviceInfo: function(_page, _row, _name) {
                var param = {
                    params: {
                        page: _page,
                        row: _row,
                        deviceName: _name
                    }
                };
                //发送get请求
                vc.http.apiGet('callingDevice.listCallingDevice',
                    param,
                    function(json) {
                        var _DeviceInfo = JSON.parse(json);
                        vc.component.chooseCallingDeviceInfo.devices = _DeviceInfo.data;
                        vc.emit('chooseCallingDevice', 'paginationPlus', 'init', {
                            total: _DeviceInfo.records,
                            dataCount: _DeviceInfo.total,
                            currentPage: _page
                        });
                    },
                    function() {
                        console.log('请求失败处理');
                    }
                );
            },
            chooseCallingDevice: function(_device) {
                vc.emit($props.emitChooseCallingDevice, 'chooseCallingDevice', _device);
                $('#chooseCallingDeviceModel').modal('hide');
            },
            queryDevices: function() {
                vc.component._loadAllDeviceInfo(1, 10, vc.component.chooseCallingDeviceInfo._currentDeviceName);
            },
            _refreshChooseCallingDeviceInfo: function() {
                vc.component.chooseCallingDeviceInfo._currentDeviceName = "";
            }
        }
    });
})(window.vc);