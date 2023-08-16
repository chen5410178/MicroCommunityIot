(function(vc) {

    vc.extends({
        data: {
            authorizeAccessControlInfo: {
                floorId: '',
                unitId: '',
                orgId: '',
                machines: [],
                machineIds: [],
                communityId: '',
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('authorizeAccessControl', 'openAddAccessControlModal', function(_accessControl) {
                $that.clearAuthAccessControlInfo();
                $that._listMachines();

                vc.copyObject(_accessControl, $that.authorizeAccessControlInfo);
                $('#authorizeAccessControlModel').modal('show');
            });
        },
        methods: {
            _authorizeAccessControl: function() {
                $that.authorizeAccessControlInfo.communityId = vc.getCurrentCommunity().communityId;
                let _machineIds = $that.authorizeAccessControlInfo.machineIds;

                if (!_machineIds || _machineIds.length < 1) {
                    vc.toast('选择授权门禁');
                    return;
                }

                let _orgId = $that.authorizeAccessControlInfo.orgId;
                let _url = "/accessControlFloor.saveAccessControlFloor";
                if (_orgId) {
                    _url = "/accessControlOrg.saveAccessControlOrg";
                }

                vc.http.apiPost(
                    _url,
                    JSON.stringify($that.authorizeAccessControlInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#authorizeAccessControlModel').modal('hide');
                            $that.clearAuthAccessControlInfo();
                            vc.emit('accessControlFloor', 'listAccessControlFloor', {});
                            vc.emit('accessControlOrg', 'listAccessControlOrg', {});
                            return;
                        }
                        vc.toast(_json.msg);

                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                        vc.toast(errInfo);
                    });
            },
            clearAuthAccessControlInfo: function() {
                $that.authorizeAccessControlInfo = {
                    floorId: '',
                    unitId: '',
                    orgId: '',
                    machines: [],
                    machineIds: [],
                    communityId: '',
                };
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
                        $that.authorizeAccessControlInfo.machines = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);