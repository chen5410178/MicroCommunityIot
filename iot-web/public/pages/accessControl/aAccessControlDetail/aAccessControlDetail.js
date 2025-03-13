/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            aAccessControlDetailInfo: {
                machineId: '',
                machineCode: '',
                machineName: '',
                communityId: '',
                machineIp: '',
                machineMac: '',
                direction: '',
                heartbeatTime: '',
                implBean: '',
                implBeanName: '',
                locationId: '',
                locationName: '',
                stateName: '',
                createTime: '',
                _currentTab: 'aAccessControlDetailFace',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.aAccessControlDetailInfo.machineId = vc.getParam('machineId');
            if (!vc.notNull($that.aAccessControlDetailInfo.machineId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.aAccessControlDetailInfo._currentTab = _currentTab;
            }

            $that._loadMachineInfo();
            $that.changeTab($that.aAccessControlDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.aAccessControlDetailInfo._currentTab);
            });
        },
        methods: {
            _loadMachineInfo: function() {
                let param = {
                        params: {
                            machineId: $that.aAccessControlDetailInfo.machineId,
                            page: 1,
                            row: 10,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/accessControl.listAdminAccessControl',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.aAccessControlDetailInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.aAccessControlDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    machineId: $that.aAccessControlDetailInfo.machineId,
                })
            },
        }
    });
})(window.vc);