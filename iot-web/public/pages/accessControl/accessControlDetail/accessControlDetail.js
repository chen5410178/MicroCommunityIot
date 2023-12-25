/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            accessControlDetailInfo: {
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
                _currentTab: 'accessControlDetailAccessControlFace',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.accessControlDetailInfo.machineId = vc.getParam('machineId');
            if (!vc.notNull($that.accessControlDetailInfo.machineId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.accessControlDetailInfo._currentTab = _currentTab;
            }

            $that._loadMachineInfo();
            $that.changeTab($that.accessControlDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.accessControlDetailInfo._currentTab);
            });
        },
        methods: {
            _loadMachineInfo: function() {
                let param = {
                        params: {
                            machineId: $that.accessControlDetailInfo.machineId,
                            page: 1,
                            row: 10,
                            communityId: vc.getCurrentCommunity().communityId,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.accessControlDetailInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.accessControlDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    machineId: $that.accessControlDetailInfo.machineId,
                })
            },
        }
    });
})(window.vc);