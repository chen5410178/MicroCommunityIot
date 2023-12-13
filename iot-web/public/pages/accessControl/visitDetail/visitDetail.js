/**
业主详情页面
 **/
(function(vc) {
    vc.extends({
        data: {
            visitDetailInfo: {
                visitId: "",
                name: "",
                visitGender: "",
                phoneNumber: '',
                communityId: '',
                roomName: '',
                ownerName: '',
                visitTypeName: '',
                visitCase: '',
                stateName: '',
                createTime: '',
                msg: '',
                _currentTab: 'visitDetailAccessControlInout',
                needBack: false,
            }
        },
        _initMethod: function() {
            $that.visitDetailInfo.visitId = vc.getParam('visitId');
            $that.visitDetailInfo.phoneNumber = vc.getParam('phoneNumber');
            if (!vc.notNull($that.visitDetailInfo.visitId)) {
                return;
            }

            let _currentTab = vc.getParam('currentTab');
            if (_currentTab) {
                $that.visitDetailInfo._currentTab = _currentTab;
            }

            $that._loadMachineInfo();
            $that.changeTab($that.visitDetailInfo._currentTab);
        },
        _initEvent: function() {
            vc.on('roomDetail', 'listRoomData', function(_info) {
                $that._loadRoomInfo();
                $that.changeTab($that.visitDetailInfo._currentTab);
            });
        },
        methods: {
            _loadMachineInfo: function() {
                let param = {
                        params: {
                            visitId: $that.visitDetailInfo.visitId,
                            page: 1,
                            row: 10,
                            communityId: vc.getCurrentCommunity().communityId,
                        }
                    }
                    //发送get请求
                vc.http.apiGet('/visit.listVisit',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        vc.copyObject(_json.data[0], $that.visitDetailInfo);
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
            changeTab: function(_tab) {
                $that.visitDetailInfo._currentTab = _tab;
                vc.emit(_tab, 'switch', {
                    tel: $that.visitDetailInfo.phoneNumber,
                })
            },
        }
    });
})(window.vc);