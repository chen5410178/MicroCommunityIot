(function (vc) {
    vc.extends({
        data: {
            addMonitorMachineOwnerInfo: {
                csId: '',
                machineId: '',
                memberId: '',
                ownerName: '',
                personType: '1001',
                role:''
            }
        },
        _initMethod: function () {
            $that.addMonitorMachineOwnerInfo.machineId = vc.getParam('machineId');
        },
        _initEvent: function () {
          
        },
        methods: {
            addMonitorMachineOwnerValidate() {
                return vc.validate.validate({
                    addMonitorMachineOwnerInfo: $that.addMonitorMachineOwnerInfo
                }, {
                    'addMonitorMachineOwnerInfo.machineId': [
                        {
                            limit: "required",
                            param: "",
                            errInfo: "设备ID不能为空"
                        }
                    ]
                });
            },
            saveMonitorMachineOwnerInfo: function () {
                
                if (!$that.addMonitorMachineOwnerValidate()) {
                    vc.toast(vc.validate.errInfo);
                    return;
                }
                $that.addMonitorMachineOwnerInfo.communityId = vc.getCurrentCommunity().communityId;
                $that.addMonitorMachineOwnerInfo.personId = $that.addMonitorMachineOwnerInfo.memberId;
                $that.addMonitorMachineOwnerInfo.personName = $that.addMonitorMachineOwnerInfo.ownerName;
                let _role = $that.addMonitorMachineOwnerInfo.role;
                if(_role == '2'){
                    $that.addMonitorMachineOwnerInfo.personId = '9999';
                    $that.addMonitorMachineOwnerInfo.personName = '全部业主';
                }
                vc.http.apiPost(
                    '/monitor.saveMonitorMachinePerson',
                    JSON.stringify($that.addMonitorMachineOwnerInfo),
                    {
                        emulateJSON: true
                    },
                    function (json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            vc.goBack();
                            vc.toast("添加成功");
                            return;
                        } else {
                            vc.toast(_json.msg);
                        }
                    },
                    function (errInfo, error) {
                        console.log('请求失败处理');
                        vc.message(errInfo);
                    });
            },
            openChooseStaff: function () {
                vc.emit('searchOwner', 'openSearchOwnerModel', {
                    callBack:function(_owner){
                        $that.addMonitorMachineOwnerInfo.memberId = _owner.memberId;
                        $that.addMonitorMachineOwnerInfo.ownerName = _owner.name;
                    }
                });


            },
            _goBack: function () {
                vc.goBack();
            }
        }
    });
})(window.vc);
