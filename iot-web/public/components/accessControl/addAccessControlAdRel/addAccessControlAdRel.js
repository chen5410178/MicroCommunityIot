(function(vc) {

    vc.extends({
        data: {
            addAccessControlAdRelInfo: {
                adId: '',
                machines: [],
                machineIds: [],
                communityId: '',
            }
        },
        _initMethod: function() {

        },
        _initEvent: function() {
            vc.on('addAccessControlAdRel', 'openAddAccessControlModal', function(_accessControl) {
                $('#addAccessControlAdRelModel').modal('show');
                $that.clearAuthAccessControlInfo();
                $that._listMachines();

                vc.copyObject(_accessControl, $that.addAccessControlAdRelInfo);

            });
        },
        methods: {
            _addAccessControlAdRel: function() {
                $that.addAccessControlAdRelInfo.communityId = vc.getCurrentCommunity().communityId;
                let _machineIds = $that.addAccessControlAdRelInfo.machineIds;

                if (!_machineIds || _machineIds.length < 1) {
                    vc.toast('选择关联门禁');
                    return;
                }

                let _url = "/ad.saveAccessControlAdRel";
           

                vc.http.apiPost(
                    _url,
                    JSON.stringify($that.addAccessControlAdRelInfo), {
                        emulateJSON: true
                    },
                    function(json, res) {
                        //vm.menus = vm.refreshMenuActive(JSON.parse(json),0);
                        let _json = JSON.parse(json);
                        if (_json.code == 0) {
                            //关闭model
                            $('#addAccessControlAdRelModel').modal('hide');
                            $that.clearAuthAccessControlInfo();
                            vc.emit('accessControlAdRel', 'listAccessControlAdRel', {});
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
                $that.addAccessControlAdRelInfo = {
                    adId: '',
                    machines: [],
                    machineIds: [],
                    communityId: '',
                };
            },
            _listMachines: function(_page, _rows) {
                let param = {
                    params: {
                        page: 1,
                        row: 1000,
                        communityId: vc.getCurrentCommunity().communityId
                    }
                };

                //发送get请求
                vc.http.apiGet('/accessControl.listAccessControl',
                    param,
                    function(json, res) {
                        let _json = JSON.parse(json);
                        $that.addAccessControlAdRelInfo.machines = _json.data;
                    },
                    function(errInfo, error) {
                        console.log('请求失败处理');
                    }
                );
            },
        }
    });

})(window.vc);