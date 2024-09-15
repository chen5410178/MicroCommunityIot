(function(vc) {
    vc.extends({
        data: {
            accessControlPersonInfo: {
                ownerId: '',
                ownerName: '',
                machines: [],
                machineIds: [],
                communityId: '',
                startDate: '',
                endDate: ''
            }
        },
        _initMethod: function() {
            $that._listMachines();
            vc.initDate('startDate', function(_value) {
                $that.accessControlPersonInfo.startDate = _value;
            });
            vc.initDate('endDate', function(_value) {
                $that.accessControlPersonInfo.endDate = _value;
            })
        },
        _initEvent: function() {
            vc.on('accessControlPerson', 'chooseOwner', function(_owner) {
                $that.accessControlPersonInfo.ownerName = _owner.name;
                $that.accessControlPersonInfo.ownerId = _owner.ownerId;
            });
        },
        methods: {

            _authorizeAccessControl: function() {
                $that.accessControlPersonInfo.communityId = vc.getCurrentCommunity().communityId;
                let _machineIds = $that.accessControlPersonInfo.machineIds;

                if (!_machineIds || _machineIds.length < 1) {
                    vc.toast('选择授权门禁');
                    return;
                }

                vc.http.apiPost(
                    '/accessControlFace.saveAccessControlFace',
                    JSON.stringify($that.accessControlPersonInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            vc.goBack();
                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            _goBack: function() {
                vc.goBack();
            },
            _openChooseOwner: function() {
                vc.emit('searchOwner', 'openSearchOwnerModel', {
                    callBack:function(_owner){
                        $that.accessControlPersonInfo.ownerId = _owner.memberId;
                        $that.accessControlPersonInfo.ownerName = _owner.name;
                    }
                });
            },
            _listMachines: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 50,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.accessControlPersonInfo.machines = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });
})(window.vc);