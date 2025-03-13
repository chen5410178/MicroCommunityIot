/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            adminMeterDetailInfo: {
                machineId: "",
                machineName: "",
                machineModelName: "",
                address: "",
                implBeanName: "",
                meterTypeName: "",
                curDegrees: "",
                curReadingTime: "",
                prestoreDegrees: "",
                readDay: "",
                readHours: "",
                stateName: "",
                roomId: "",
                roomName: "",
                heartbeatTime: "",
                createTime: "",
                specs: [],
                _currentTab: 'adminMeterDetailRoom',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.adminMeterDetailInfo.machineId = vc.getParam('machineId');
            $that.adminMeterDetailInfo.roomId = vc.getParam('roomId');
            if (!vc.notNull($that.adminMeterDetailInfo.machineId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.adminMeterDetailInfo._currentTab = _currentTab;
            }

            $that._loadMachineInfo();
            $that.changeTab($that.adminMeterDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.adminMeterDetailInfo._currentTab);
            });
        },
        methods: {
            _loadMachineInfo: function() {
                let param = {
                        params: {
                            machineId: $that.adminMeterDetailInfo.machineId,
                            page: 1,
                            row: 10,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/meterMachine.listAdminMeterMachine',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.adminMeterDetailInfo);
                        _json.data[0].machineModel === 1001 ? $that.adminMeterDetailInfo.machineModelName = "充值模式" : $that.adminMeterDetailInfo.machineModelName = "抄表模式";
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.adminMeterDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    machineId: $that.adminMeterDetailInfo.machineId,
                    machineName: $that.adminMeterDetailInfo.machineName,
                    roomId: $that.adminMeterDetailInfo.roomId,
                })
            },
        }
    });
})(window.vc);